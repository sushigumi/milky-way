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
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdatePendingTestOperation extends Operation<Test> {
  private final Logger LOGGER = LoggerFactory.getLogger(UpdatePendingTestOperation.class);

  private final String testId;
  private final TestStatus newStatus;

  public UpdatePendingTestOperation(String testId, TestStatus newStatus) {
    this.testId = testId;
    this.newStatus = newStatus;
  }

  private Test updateStatus(TestRepository testRepository) {
    final Bson filter =
        Filters.and(Filters.eq("_id", testId), Filters.eq("status", TestStatus.PENDING));
    final Bson updates = Updates.set("status", newStatus.name());

    final FindOneAndUpdateOptions options =
        new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);

    return testRepository.mongoCollection().findOneAndUpdate(filter, updates, options);
  }

  private Test updateTestCommitHash(TestRepository testRepository, String commitHash) {
    final Bson filter =
        Filters.and(Filters.eq("_id", testId), Filters.eq("status", TestStatus.RUNNING));
    final Bson updates = Updates.set("commitHash", commitHash);

    final FindOneAndUpdateOptions options =
        new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);

    return testRepository.mongoCollection().findOneAndUpdate(filter, updates, options);
  }

  @Override
  public void execute(OperationContext context) {
    final TestRepository testRepository = context.getTestRepository();
    Test test = updateStatus(testRepository);
    if (test == null) {
      LOGGER.error("Unable to update status for test {}.", testId);
      return;
    }

    // Determine the commit hash of the test template that is used to run the test.
    final String commitHash =
        context.getCrdTemplateService().getTestTemplateCommitHashByName(test.name);
    if (commitHash == null) {
      LOGGER.error("Unable to find commit has for test template {}.", test.name);
      return;
    }

    // Update the test commit hash so that we own a record of which template was used to run the
    // test.
    test = updateTestCommitHash(testRepository, commitHash);
    if (test == null) {
      LOGGER.error("Unable to update test commit hash for test {}.", testId);
      return;
    }

    result = test;
  }
}
