package dev.sushigumi.milkyway.kubernetes;

import dev.sushigumi.milkyway.database.entities.Test;
import dev.sushigumi.milkyway.kubernetes.api.model.TestTemplate;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@ApplicationScoped
public class TestExecutorService {
  private static final Logger LOGGER = LoggerFactory.getLogger(TestExecutorService.class);

  private final KubernetesClient client;
  private Watch jobWatch;

  public TestExecutorService(KubernetesClient client) {
    this.client = client;
  }

  @PostConstruct
  void init() {
    LOGGER.info("Registering kubernetes job watcher.");
    jobWatch = client.batch().v1().jobs().watch(new JobWatcher());
  }

  @PreDestroy
  void destroy() {
    if (jobWatch != null) {
      LOGGER.info("Closing kubernetes job watcher.");
      jobWatch.close();
    }
  }

  public void executeTest(Test test) {
    final var testTemplate = client.resources(TestTemplate.class).withName(test.name).get();
    final var testTemplateSpec = testTemplate.getSpec();
    final Job job = new JobBuilder()
            .withApiVersion("batch/v1")
            .withNewMetadata()
            .withGenerateName(testTemplate.getMetadata().getName())
            .endMetadata()
            .withNewSpec()
            .withNewTemplate()
            .withNewSpec()
            .withRestartPolicy("Never")
            .endSpec()
            .endTemplate()
            .endSpec().build();

    // Patch the job with the container.
    job.getSpec().getTemplate().getSpec().getContainers().add(testTemplateSpec.getContainer());

    client.batch().v1().jobs().resource(job).create();
  }
}
