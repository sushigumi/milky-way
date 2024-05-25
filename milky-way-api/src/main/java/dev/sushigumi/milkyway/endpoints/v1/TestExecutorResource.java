package dev.sushigumi.milkyway.endpoints.v1;

import dev.sushigumi.milkyway.database.entities.Test;
import dev.sushigumi.milkyway.database.entities.TestStatus;
import dev.sushigumi.milkyway.endpoints.v1.api.TestExecuteRequest;
import dev.sushigumi.milkyway.operations.execute.ExecuteTestOperation;
import dev.sushigumi.milkyway.operations.read.GetTestOperation;
import dev.sushigumi.milkyway.operations.update.UpdateTestStatusOperation;
import dev.sushigumi.milkyway.services.OperationExecutorService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/api/v1/execute")
public class TestExecutorResource {
  private final OperationExecutorService executorService;

  public TestExecutorResource(OperationExecutorService executorService) {
    this.executorService = executorService;
  }

  @POST
  public void executeTest(TestExecuteRequest request) {
    final Test test =
        executorService.execute(
            new UpdateTestStatusOperation(request.getTestPlanId(), TestStatus.RUNNING));
    if (test == null) {
      if (executorService.execute(new GetTestOperation(request.getTestPlanId())) == null) {
        throw new NotFoundException("Test could not be found.");
      } else {
        throw new BadRequestException("Test has already been queued.");
      }
    }

    executorService.execute(new ExecuteTestOperation(test));
  }
}
