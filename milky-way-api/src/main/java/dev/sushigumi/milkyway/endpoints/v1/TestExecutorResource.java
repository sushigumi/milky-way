package dev.sushigumi.milkyway.endpoints.v1;

import dev.sushigumi.milkyway.endpoints.v1.api.TestExecuteRequest;
import dev.sushigumi.milkyway.exceptions.TestStatusUpdateException;
import dev.sushigumi.milkyway.services.TestExecutorService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/api/v1/execute")
public class TestExecutorResource {
  private final TestExecutorService testExecutorService;

  public TestExecutorResource(TestExecutorService testExecutorService) {
    this.testExecutorService = testExecutorService;
  }

  @POST
  public void executeTest(TestExecuteRequest request) {
    try {
      testExecutorService.executeTestPlan(request.getTestPlanId());
    } catch (TestStatusUpdateException e) {
      throw new BadRequestException();
    }
  }
}
