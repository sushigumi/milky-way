package dev.sushigumi.milkyway.operations.read;

import dev.sushigumi.milkyway.database.TestPlanRepository;
import dev.sushigumi.milkyway.database.projections.TestPlanSummary;
import dev.sushigumi.milkyway.operations.Operation;
import dev.sushigumi.milkyway.operations.OperationContext;
import java.util.List;

public class GetTestPlanSummariesOperation extends Operation<List<TestPlanSummary>> {
  @Override
  public void execute(OperationContext context) {
    final TestPlanRepository testPlanRepository = context.getTestPlanRepository();
    result = testPlanRepository.getAllTestPlanSummaries();
  }
}
