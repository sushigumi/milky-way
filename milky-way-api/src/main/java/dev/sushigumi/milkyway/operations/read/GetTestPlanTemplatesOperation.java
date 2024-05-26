package dev.sushigumi.milkyway.operations.read;

import dev.sushigumi.milkyway.kubernetes.api.model.TestPlanTemplate;
import dev.sushigumi.milkyway.operations.Operation;
import dev.sushigumi.milkyway.operations.OperationContext;
import dev.sushigumi.milkyway.services.CrdTemplateService;
import java.util.List;

public class GetTestPlanTemplatesOperation extends Operation<List<TestPlanTemplate>> {
  @Override
  public void execute(OperationContext context) {
    final CrdTemplateService crdTemplateService = context.getCrdTemplateService();
    result = crdTemplateService.getAllTestPlanTemplates();
  }
}
