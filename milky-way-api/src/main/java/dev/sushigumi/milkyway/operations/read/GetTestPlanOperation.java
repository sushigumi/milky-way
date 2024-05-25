package dev.sushigumi.milkyway.operations.read;

import dev.sushigumi.milkyway.database.TestPlanRepository;
import dev.sushigumi.milkyway.database.entities.TestPlan;
import dev.sushigumi.milkyway.operations.Operation;
import dev.sushigumi.milkyway.operations.OperationContext;
import jakarta.ws.rs.NotFoundException;
import org.bson.types.ObjectId;

public class GetTestPlanOperation extends Operation<TestPlan> {
  private final String testPlanId;

  public GetTestPlanOperation(String testPlanId) {
    this.testPlanId = testPlanId;
  }

  @Override
  public void execute(OperationContext context) {
    final TestPlanRepository testPlanRepository = context.getTestPlanRepository();
    final TestPlan testPlan = testPlanRepository.findById(new ObjectId(testPlanId));
    if (testPlan == null) {
      throw new NotFoundException("Test plan not found.");
    }

    result = testPlan;
  }
}
