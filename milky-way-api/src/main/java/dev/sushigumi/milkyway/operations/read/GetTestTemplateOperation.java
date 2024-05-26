package dev.sushigumi.milkyway.operations.read;

import dev.sushigumi.milkyway.kubernetes.api.model.TestTemplate;
import dev.sushigumi.milkyway.operations.Operation;
import dev.sushigumi.milkyway.operations.OperationContext;
import jakarta.ws.rs.NotFoundException;

public class GetTestTemplateOperation extends Operation<TestTemplate> {
  private final String testTemplateName;

  public GetTestTemplateOperation(String testTemplateName) {
    this.testTemplateName = testTemplateName;
  }

  @Override
  public void execute(OperationContext context) {
    final var resource = context.getCrdTemplateService().getTestTemplateByName(testTemplateName);
    if (resource == null) {
      throw new NotFoundException("Test template not found.");
    }

    result = resource;
  }
}
