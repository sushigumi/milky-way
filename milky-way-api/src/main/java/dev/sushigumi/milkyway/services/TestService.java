package dev.sushigumi.milkyway.services;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import dev.sushigumi.milkyway.JobWatcher;
import dev.sushigumi.milkyway.database.TestPlanRepository;
import dev.sushigumi.milkyway.database.TestRepository;
import dev.sushigumi.milkyway.database.entities.Test;
import dev.sushigumi.milkyway.database.entities.TestPlan;
import dev.sushigumi.milkyway.database.entities.TestStatus;
import dev.sushigumi.milkyway.kubernetes.api.model.TestTemplate;
import dev.sushigumi.milkyway.kubernetes.api.model.TestTemplateSpec;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class TestService {
  private static final Logger LOGGER = LoggerFactory.getLogger(TestService.class);

  private final TestRepository testRepository;
  private final TestPlanRepository testPlanRepository;
  private final KubernetesClient k8sClient;

  private Watch jobWatch;

  public TestService(
      TestRepository testRepository,
      TestPlanRepository testPlanRepository,
      KubernetesClient k8sClient) {
    this.testRepository = testRepository;
    this.testPlanRepository = testPlanRepository;
    this.k8sClient = k8sClient;
  }

  @PostConstruct
  void init() {
    LOGGER.info("Registering kubernetes job watcher.");
    jobWatch = k8sClient.batch().v1().jobs().watch(new JobWatcher(this));
  }

  @PreDestroy
  void destroy() {
    if (jobWatch != null) {
      LOGGER.info("Closing kubernetes job watcher.");
      jobWatch.close();
    }
  }

  /**
   * Get a test by its ID.
   *
   * @param id The string representation of the object ID.
   * @return The test that was found. If no documents match, then null will be returned.
   */
  public Test getTestById(String id) {
    return testRepository.findById(new ObjectId(id));
  }

  /**
   * Get a test plan by its ID.
   *
   * @param id The string representation of the object ID.
   * @return The test plan that was found. If no documents match, then null will be returned.
   */
  public TestPlan getTestPlanById(String id) {
    return testPlanRepository.findById(new ObjectId(id));
  }

  /**
   * Get a list of tests that belong to a test plan with the specified ID.
   *
   * @param id The string representation of the object ID.
   * @return The list of tests that were found. If no tests were found, then the list will be empty.
   */
  public List<Test> getTestsByTestPlanId(String id) {
    final TestPlan testPlan = getTestPlanById(id);
    final var query = new Document("_id", new Document("$in", testPlan.testIds));
    return testRepository.find(query).list();
  }

  public Test updateTestStatus(String id, TestStatus newStatus) {
    final Bson filter =
        Filters.and(
            Filters.eq("_id", new ObjectId(id)), Filters.eq("status", TestStatus.PENDING.name()));
    final Bson updates = Updates.set("status", newStatus.name());
    return testRepository.mongoCollection().findOneAndUpdate(filter, updates);
  }

  private void submitJob(String testId, TestTemplate template) {
    final TestTemplateSpec spec = template.getSpec();
    final Map<String, String> annotations = new HashMap<>();
    annotations.put("testId", testId);
    final Job job =
        new JobBuilder()
            .withApiVersion("batch/v1")
            .withNewMetadata()
            .withGenerateName(template.getMetadata().getName() + "-")
            .withAnnotations(annotations)
            .endMetadata()
            .withNewSpec()
            .withNewTemplate()
            .withNewSpec()
            .withRestartPolicy("Never")
            .endSpec()
            .endTemplate()
            .endSpec()
            .build();

    // Add the container to the job.
    job.getSpec().getTemplate().getSpec().getContainers().add(spec.getContainer());

    k8sClient.batch().v1().jobs().resource(job).create();
  }

  // TODO: Change the return to throws.
  public boolean executeTest(String id) {
    // Try to update the status of the test first. If the status cannot be updated, then it is
    // already running. Return to avoid duplicate runs.
    final Test test = updateTestStatus(id, TestStatus.RUNNING);
    if (test == null) {
      return false;
    }

    final TestTemplate template = k8sClient.resources(TestTemplate.class).withName(test.name).get();
    if (template == null) {
      return false;
    }
    submitJob(id, template);

    return true;
  }
}
