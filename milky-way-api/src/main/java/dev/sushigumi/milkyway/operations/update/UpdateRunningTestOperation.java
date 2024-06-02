package dev.sushigumi.milkyway.operations.update;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import dev.sushigumi.milkyway.database.TestRepository;
import dev.sushigumi.milkyway.database.entities.Test;
import dev.sushigumi.milkyway.database.entities.TestStatus;
import dev.sushigumi.milkyway.operations.Operation;
import dev.sushigumi.milkyway.operations.OperationContext;
import jakarta.ws.rs.NotFoundException;
import org.bson.conversions.Bson;

public class UpdateRunningTestOperation extends Operation<Test> {
  private final String testId;
  private final TestStatus newStatus;

  public UpdateRunningTestOperation(String testId, TestStatus newStatus) {
    this.testId = testId;
    this.newStatus = newStatus;
  }

  @Override
  public void execute(OperationContext context) {
    final TestRepository testRepository = context.getTestRepository();
    final Bson filter =
        Filters.and(Filters.eq("_id", testId), Filters.eq("status", TestStatus.RUNNING));
    final Bson updates = Updates.set("status", newStatus.name());

    final FindOneAndUpdateOptions options =
        new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);
    final Test newTest =
        testRepository.mongoCollection().findOneAndUpdate(filter, updates, options);
    if (newTest == null) {
      throw new NotFoundException();
    }

    result = newTest;
  }
}
