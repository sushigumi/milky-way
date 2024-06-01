package dev.sushigumi.milkyway.operations.read;

import dev.sushigumi.milkyway.kubernetes.api.model.TestPlanTemplate;
import dev.sushigumi.milkyway.operations.Operation;
import dev.sushigumi.milkyway.operations.OperationContext;
import jakarta.ws.rs.NotFoundException;

public class GetTestPlanTemplateOperation extends Operation<TestPlanTemplate> {
  private final String templateName;

  public GetTestPlanTemplateOperation(String templateName) {
    this.templateName = templateName;
  }

  @Override
  public void execute(OperationContext context) {
    final var resource = context.getCrdTemplateService().getTestPlanTemplateByName(templateName);
    if (resource == null) {
      throw new NotFoundException("Test plan template not found.");
    }

    result = resource;
  }
}
