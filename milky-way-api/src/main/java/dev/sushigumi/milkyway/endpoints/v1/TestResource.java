package dev.sushigumi.milkyway.endpoints.v1;

import dev.sushigumi.milkyway.database.entities.Test;
import dev.sushigumi.milkyway.database.entities.TestPlan;
import dev.sushigumi.milkyway.database.entities.TestStatus;
import dev.sushigumi.milkyway.endpoints.v1.api.ExecuteTestsRequest;
import dev.sushigumi.milkyway.endpoints.v1.api.ExecuteTestsResponse;
import dev.sushigumi.milkyway.operations.execute.ExecuteTestOperation;
import dev.sushigumi.milkyway.operations.read.GetTestOperation;
import dev.sushigumi.milkyway.operations.read.GetTestPlanOperation;
import dev.sushigumi.milkyway.operations.read.GetTestsOperation;
import dev.sushigumi.milkyway.operations.update.UpdatePendingTestOperation;
import dev.sushigumi.milkyway.services.OperationExecutorService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

@Path("/api/v1/tests")
public class TestResource {
  private final OperationExecutorService executorService;

  public TestResource(OperationExecutorService executorService) {
    this.executorService = executorService;
  }

  @Path("/")
  public List<Test> getTestsForTestPlan(@NotEmpty @QueryParam("testPlanId") String testPlanId) {
    final TestPlan testPlan = executorService.execute(new GetTestPlanOperation(testPlanId));
    if (testPlan == null) {
      throw new NotFoundException("Test plan not found");
    }

    return executorService.execute(
        new GetTestsOperation(new GetTestsOperation.WithTestPlanQuery(testPlan)));
  }

  @Path("/:testId/execute")
  @POST
  public ExecuteTestsResponse executeTest(ExecuteTestsRequest request) {
    final List<String> invalidTestIds = new ArrayList<>();
    final List<String> queuedTestIds = new ArrayList<>();
    final List<Test> validTests = new ArrayList<>();
    for (var testId : request.getTestIds()) {
      final Test test =
          executorService.execute(new UpdatePendingTestOperation(testId, TestStatus.RUNNING));
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
