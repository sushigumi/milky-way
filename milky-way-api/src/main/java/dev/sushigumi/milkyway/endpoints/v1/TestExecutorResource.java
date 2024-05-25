package dev.sushigumi.milkyway.endpoints.v1;

import dev.sushigumi.milkyway.database.entities.Test;
import dev.sushigumi.milkyway.database.entities.TestStatus;
import dev.sushigumi.milkyway.endpoints.v1.api.ExecuteTestsRequest;
import dev.sushigumi.milkyway.endpoints.v1.api.ExecuteTestsResponse;
import dev.sushigumi.milkyway.operations.execute.ExecuteTestOperation;
import dev.sushigumi.milkyway.operations.read.GetTestOperation;
import dev.sushigumi.milkyway.operations.update.UpdateTestStatusOperation;
import dev.sushigumi.milkyway.services.OperationExecutorService;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import java.util.ArrayList;
import java.util.List;

@Path("/api/v1/execute")
public class TestExecutorResource {
  private final OperationExecutorService executorService;

  public TestExecutorResource(OperationExecutorService executorService) {
    this.executorService = executorService;
  }

  @POST
  public ExecuteTestsResponse executeTest(ExecuteTestsRequest request) {
    final List<String> invalidTestIds = new ArrayList<>();
    final List<String> queuedTestIds = new ArrayList<>();
    final List<Test> validTests = new ArrayList<>();
    for (var testId : request.getTestIds()) {
      final Test test =
          executorService.execute(new UpdateTestStatusOperation(testId, TestStatus.RUNNING));
      if (test == null) {
        if (executorService.execute(new GetTestOperation(testId)) == null) {
          invalidTestIds.add(testId);
        } else {
          queuedTestIds.add(testId);
        }
      } else {
        validTests.add(test);
      }
    }

    // Execute all valid test ids.
    for (var test : validTests) {
      executorService.execute(new ExecuteTestOperation(test));
    }

    return new ExecuteTestsResponse(invalidTestIds, queuedTestIds);
  }
}
