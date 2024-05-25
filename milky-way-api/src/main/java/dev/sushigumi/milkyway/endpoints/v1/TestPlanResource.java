package dev.sushigumi.milkyway.endpoints.v1;

import dev.sushigumi.milkyway.database.entities.TestPlan;
import dev.sushigumi.milkyway.database.projections.TestPlanSummary;
import dev.sushigumi.milkyway.endpoints.v1.api.TestPlanCreateRequest;
import dev.sushigumi.milkyway.operations.create.CreateTestPlanOperation;
import dev.sushigumi.milkyway.operations.delete.DeleteTestPlanOperation;
import dev.sushigumi.milkyway.operations.read.GetTestPlanOperation;
import dev.sushigumi.milkyway.operations.read.GetTestPlanSummariesOperation;
import dev.sushigumi.milkyway.services.OperationExecutorService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import java.util.List;

@Path("/api/v1/plans")
class TestPlanResource {
  private final OperationExecutorService executorService;

  public TestPlanResource(OperationExecutorService executorService) {
    this.executorService = executorService;
  }

  @Path("/")
  @GET
  public List<TestPlanSummary> getTestPlanSummaries() {
    return executorService.execute(new GetTestPlanSummariesOperation());
  }

  @Path("/{planId}")
  @GET
  public TestPlan getTestPlan(@PathParam("planId") String planId) {
    return executorService.execute(new GetTestPlanOperation(planId));
  }

  @Path("/")
  @POST
  public TestPlan createTestPlan(@Valid TestPlanCreateRequest request) {
    return executorService.execute(
        new CreateTestPlanOperation(request.getConfigurationId(), request.getName()));
  }

  @Path("/{planId}")
  @DELETE
  public void deleteTestPlan(@PathParam("planId") String planId) {
    executorService.execute(new DeleteTestPlanOperation(planId));
  }
}
