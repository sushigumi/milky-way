package dev.sushigumi.milkyway.endpoints.v1;

import dev.sushigumi.milkyway.database.entities.TestPlanConfiguration;
import dev.sushigumi.milkyway.services.TestService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.List;

@Path("/api/v1/plan-configs")
public class TestPlanConfigurationResource {
  private final TestService testService;

  public TestPlanConfigurationResource(TestService testService) {
    this.testService = testService;
  }

  @Path("/")
  @GET
  public List<TestPlanConfiguration> getTestPlanConfigurations() {
    return testService.getAllTestPlanConfigurations();
  }
}
