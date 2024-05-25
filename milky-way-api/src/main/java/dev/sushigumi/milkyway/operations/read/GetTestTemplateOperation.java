package dev.sushigumi.milkyway.operations.read;

import dev.sushigumi.milkyway.kubernetes.api.model.TestTemplate;
import dev.sushigumi.milkyway.operations.Operation;
import dev.sushigumi.milkyway.operations.OperationContext;
import io.fabric8.kubernetes.client.KubernetesClient;
import jakarta.ws.rs.NotFoundException;

public class GetTestTemplateOperation extends Operation<TestTemplate> {
  private final String testTemplateName;

  public GetTestTemplateOperation(String testTemplateName) {
    this.testTemplateName = testTemplateName;
  }

  @Override
  public void execute(OperationContext context) {
    final KubernetesClient k8sClient = context.getK8sClient();
    final var resource = k8sClient.resources(TestTemplate.class).withName(testTemplateName).get();
    if (resource == null) {
      throw new NotFoundException("Test template not found.");
    }

    result = resource;
  }
}
