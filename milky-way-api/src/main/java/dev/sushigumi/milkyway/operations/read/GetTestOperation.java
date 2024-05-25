package dev.sushigumi.milkyway.operations.read;

import dev.sushigumi.milkyway.database.TestRepository;
import dev.sushigumi.milkyway.database.entities.Test;
import dev.sushigumi.milkyway.operations.Operation;
import dev.sushigumi.milkyway.operations.OperationContext;
import jakarta.ws.rs.NotFoundException;
import org.bson.types.ObjectId;

public class GetTestOperation extends Operation<Test> {
  private final String testId;

  public GetTestOperation(String testId) {
    this.testId = testId;
  }

  @Override
  public void execute(OperationContext context) {
    final TestRepository testRepository = context.getTestRepository();
    final Test test = testRepository.findById(new ObjectId(testId));
    if (test == null) {
      throw new NotFoundException("Test not found.");
    }

    result = test;
  }
}
