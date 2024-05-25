package dev.sushigumi.milkyway.operations.execute;

import dev.sushigumi.milkyway.database.entities.Test;
import dev.sushigumi.milkyway.kubernetes.api.model.TestTemplate;
import dev.sushigumi.milkyway.kubernetes.api.model.TestTemplateSpec;
import dev.sushigumi.milkyway.operations.Operation;
import dev.sushigumi.milkyway.operations.OperationContext;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import jakarta.ws.rs.BadRequestException;
import java.util.HashMap;
import java.util.Map;

public class ExecuteTestOperation extends Operation<Void> {
  private final Test test;

  public ExecuteTestOperation(Test test) {
    this.test = test;
  }

  private void submitJob(KubernetesClient k8sClient, TestTemplate template) {
    final TestTemplateSpec spec = template.getSpec();
    final Map<String, String> annotations = new HashMap<>();
    annotations.put("testId", test.id.toHexString());
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

  @Override
  public void execute(OperationContext context) {
    final KubernetesClient k8sClient = context.getK8sClient();
    final TestTemplate template = k8sClient.resources(TestTemplate.class).withName(test.name).get();
    if (template == null) {
      throw new BadRequestException("Could not find template for test.");
    }

    submitJob(k8sClient, template);
  }
}
