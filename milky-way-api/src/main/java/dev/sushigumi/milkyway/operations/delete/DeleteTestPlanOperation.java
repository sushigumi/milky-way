package dev.sushigumi.milkyway.operations.delete;

import dev.sushigumi.milkyway.database.TestPlanRepository;
import dev.sushigumi.milkyway.operations.Operation;
import dev.sushigumi.milkyway.operations.OperationContext;
import jakarta.ws.rs.NotFoundException;
import org.bson.types.ObjectId;

public class DeleteTestPlanOperation extends Operation<Void> {
  private final String testPlanId;

  public DeleteTestPlanOperation(String testPlanId) {
    this.testPlanId = testPlanId;
  }

  @Override
  public void execute(OperationContext context) {
    final TestPlanRepository testPlanRepository = context.getTestPlanRepository();
    if (!testPlanRepository.deleteById(new ObjectId(testPlanId))) {
      throw new NotFoundException();
    }
  }
}
