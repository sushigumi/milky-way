package dev.sushigumi.milkyway.endpoints.v1;

import dev.sushigumi.milkyway.endpoints.v1.api.TestExecuteRequest;
import dev.sushigumi.milkyway.services.TestService;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/api/v1/execute")
public class TestExecutorResource {
  private final TestService testService;

  public TestExecutorResource(TestService testService) {
    this.testService = testService;
  }

  @POST
  public void executeTest(TestExecuteRequest request) {
    testService.executeTest("asdf");
  }
}
