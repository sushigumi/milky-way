package dev.sushigumi.milkyway.operations.create;

import dev.sushigumi.milkyway.database.TestPlanRepository;
import dev.sushigumi.milkyway.database.TestRepository;
import dev.sushigumi.milkyway.database.entities.Test;
import dev.sushigumi.milkyway.database.entities.TestPlan;
import dev.sushigumi.milkyway.database.entities.TestStatus;
import dev.sushigumi.milkyway.kubernetes.api.model.TestPlanTemplate;
import dev.sushigumi.milkyway.operations.Operation;
import dev.sushigumi.milkyway.operations.OperationContext;
import jakarta.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

public class CreateTestPlanOperation extends Operation<TestPlan> {
  private final String templateName;
  private final String testPlanName;

  public CreateTestPlanOperation(String templateName, String testPlanName) {
    this.templateName = templateName;
    this.testPlanName = testPlanName;
  }

  @Override
  public void execute(OperationContext context) {
    final TestPlanRepository testPlanRepository = context.getTestPlanRepository();
    final TestRepository testRepository = context.getTestRepository();

    final TestPlanTemplate template =
        context.getCrdTemplateService().getTestPlanTemplateByName(templateName);
    if (template == null) {
      throw new BadRequestException("Test plan configuration does not exist.");
    }

    List<ObjectId> testIds = new ArrayList<>(template.getSpec().getTestTemplates().length);
    for (var testTemplateName : template.getSpec().getTestTemplates()) {
      // Don't set the commit hash first. The commit hash will only be known on test execution.
      final var test = new Test();
      test.name = testTemplateName;
      test.status = TestStatus.PENDING;

      testRepository.persist(test);
      testIds.add(test.id);
    }

    // Create the test plan and persist it to database.
    final var testPlan = new TestPlan();
    testPlan.name = testPlanName;
    testPlan.testIds = testIds;

    testPlanRepository.persist(testPlan);

    result = testPlan;
  }
}
