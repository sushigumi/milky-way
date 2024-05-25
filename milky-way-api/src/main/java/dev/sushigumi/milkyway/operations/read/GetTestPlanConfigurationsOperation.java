package dev.sushigumi.milkyway.operations.read;

import dev.sushigumi.milkyway.database.TestPlanConfigurationRepository;
import dev.sushigumi.milkyway.database.entities.TestPlanConfiguration;
import dev.sushigumi.milkyway.operations.Operation;
import dev.sushigumi.milkyway.operations.OperationContext;
import java.util.List;

public class GetTestPlanConfigurationsOperation extends Operation<List<TestPlanConfiguration>> {
  @Override
  public void execute(OperationContext context) {
    final TestPlanConfigurationRepository testPlanConfigurationRepository =
        context.getTestPlanConfigurationRepository();
    result = testPlanConfigurationRepository.listAll();
  }
}
