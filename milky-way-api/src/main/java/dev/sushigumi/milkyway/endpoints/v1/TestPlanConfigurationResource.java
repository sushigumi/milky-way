package dev.sushigumi.milkyway.endpoints.v1;

import dev.sushigumi.milkyway.database.entities.TestPlanConfiguration;
import dev.sushigumi.milkyway.operations.read.GetTestPlanConfigurationsOperation;
import dev.sushigumi.milkyway.services.OperationExecutorService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.List;

@Path("/api/v1/plan-configs")
public class TestPlanConfigurationResource {
  private final OperationExecutorService executorService;

  public TestPlanConfigurationResource(OperationExecutorService executorService) {
    this.executorService = executorService;
  }

  @Path("/")
  @GET
  public List<TestPlanConfiguration> getTestPlanConfigurations() {
    final var operation = new GetTestPlanConfigurationsOperation();
    executorService.execute(operation);

    return operation.getResult();
  }
}
