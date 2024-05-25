package dev.sushigumi.milkyway.operations.create;

import dev.sushigumi.milkyway.database.TestPlanConfigurationRepository;
import dev.sushigumi.milkyway.database.TestPlanRepository;
import dev.sushigumi.milkyway.database.TestRepository;
import dev.sushigumi.milkyway.database.entities.Test;
import dev.sushigumi.milkyway.database.entities.TestPlan;
import dev.sushigumi.milkyway.database.entities.TestPlanConfiguration;
import dev.sushigumi.milkyway.database.entities.TestStatus;
import dev.sushigumi.milkyway.operations.Operation;
import dev.sushigumi.milkyway.operations.OperationContext;
import jakarta.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

public class CreateTestPlanOperation extends Operation<TestPlan> {
  private final String configurationId;
  private final String testPlanName;

  public CreateTestPlanOperation(String configurationId, String testPlanName) {
    this.configurationId = configurationId;
    this.testPlanName = testPlanName;
  }

  @Override
  public void execute(OperationContext context) {
    final TestPlanConfigurationRepository testPlanConfigurationRepository =
        context.getTestPlanConfigurationRepository();
    final TestPlanRepository testPlanRepository = context.getTestPlanRepository();
    final TestRepository testRepository = context.getTestRepository();

    final TestPlanConfiguration configuration =
        testPlanConfigurationRepository.findById(new ObjectId(configurationId));
    if (configuration == null) {
      throw new BadRequestException("Test plan configuration does not exist.");
    }

    List<ObjectId> testIds = new ArrayList<>(configuration.testTemplates.size());
    for (var template : configuration.testTemplates) {
      final var test = new Test();
      test.name = template;
      test.status = TestStatus.PENDING;

      testRepository.persist(test);
      testIds.add(test.id);
    }

    // Create the test plan and persist it to database.
    final var testPlan = new TestPlan();
    testPlan.name = testPlanName;
    testPlan.testIds = testIds;

    testPlanRepository.persist(testPlan);
  }
}
