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
import java.util.ArrayList;
import java.util.List;
import org.bson.conversions.Bson;

public class UpdatePendingTestOperation extends Operation<Test> {
  private final String testId;
  private final TestStatus newStatus;
  private final String resourceCommitHash;

  public UpdatePendingTestOperation(
      String testId, TestStatus newStatus, String resourceCommitHash) {
    this.testId = testId;
    this.newStatus = newStatus;
    this.resourceCommitHash = resourceCommitHash;
  }

  private List<Bson> getUpdates() {
    final List<Bson> updates = new ArrayList<>();
    updates.add(Updates.set("status", newStatus.name()));

    if (resourceCommitHash != null) {
      updates.add(Updates.set("commitHash", resourceCommitHash));
    }

    return updates;
  }

  @Override
  public void execute(OperationContext context) {
    final TestRepository testRepository = context.getTestRepository();
    final Bson filter =
        Filters.and(Filters.eq("_id", testId), Filters.eq("status", TestStatus.PENDING));
    final Bson updates = Updates.combine(getUpdates());

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
