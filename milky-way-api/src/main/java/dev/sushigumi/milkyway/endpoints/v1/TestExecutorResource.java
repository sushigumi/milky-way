package dev.sushigumi.milkyway.endpoints.v1;

import dev.sushigumi.milkyway.database.entities.Test;
import dev.sushigumi.milkyway.database.entities.TestStatus;
import dev.sushigumi.milkyway.kubernetes.TestExecutorService;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/api/v1/execute")
public class TestExecutorResource {
  private final TestExecutorService testExecutorService;

  public TestExecutorResource(TestExecutorService testExecutorService) {
    this.testExecutorService = testExecutorService;
  }

  @POST
  public void executeTest() {
    final var test = new Test();
    test.name = "dummy-test-job";
    test.group = "thedog";
    test.status = TestStatus.PENDING;
    testExecutorService.executeTest(test);
  }
}
