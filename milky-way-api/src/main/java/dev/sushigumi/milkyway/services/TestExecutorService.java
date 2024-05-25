package dev.sushigumi.milkyway.services;

import dev.sushigumi.milkyway.JobWatcher;
import dev.sushigumi.milkyway.database.entities.TestPlan;
import dev.sushigumi.milkyway.database.entities.TestStatus;
import dev.sushigumi.milkyway.exceptions.TestStatusUpdateException;
import dev.sushigumi.milkyway.kubernetes.api.model.TestTemplate;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@ApplicationScoped
public class TestExecutorService {
  private static final Logger LOGGER = LoggerFactory.getLogger(TestExecutorService.class);

  private final KubernetesClient client;
  private final TestPlanService testPlanService;
  private Watch jobWatch;

  public TestExecutorService(KubernetesClient client, TestPlanService testPlanService) {
    this.client = client;
    this.testPlanService = testPlanService;
  }

  @PostConstruct
  void init() {
    LOGGER.info("Registering kubernetes job watcher.");
    jobWatch = client.batch().v1().jobs().watch(new JobWatcher(testPlanService));
  }

  @PreDestroy
  void destroy() {
    if (jobWatch != null) {
      LOGGER.info("Closing kubernetes job watcher.");
      jobWatch.close();
    }
  }

  private void submitJob(String testPlanId, TestTemplate template) {
    final var testTemplateSpec = template.getSpec();
    final Map<String, String> annotations = new HashMap<>();
    annotations.put("testPlanId", testPlanId);
    annotations.put("testName", template.getMetadata().getName());
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

    // Patch the job with the container.
    job.getSpec().getTemplate().getSpec().getContainers().add(testTemplateSpec.getContainer());

    client.batch().v1().jobs().resource(job).create();
  }

  public void executeTestPlan(TestPlan testPlan) throws TestStatusUpdateException {
    // Make an update immediately to the service so that no other deployments of this service will
    // schedule a duplicate run.
    testPlanService.updateTestStatus(
        testPlan.id.toHexString(), "dummy-test-job", TestStatus.RUNNING);
    for (final var test : testPlan.tests) {
      final TestTemplate template = client.resources(TestTemplate.class).withName(test.name).get();
      submitJob(testPlan.id.toHexString(), template);
    }
  }

  public void executeTestPlan(String testPlanId) throws TestStatusUpdateException {
    executeTestPlan(testPlanService.getTestPlanById(testPlanId));
  }
}
