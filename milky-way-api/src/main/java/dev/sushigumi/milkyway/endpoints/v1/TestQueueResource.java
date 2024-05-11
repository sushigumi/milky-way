package dev.sushigumi.milkyway.endpoints.v1;

import dev.sushigumi.milkyway.core.database.TestPlanRepository;
import dev.sushigumi.milkyway.core.database.entities.TestPlan;
import dev.sushigumi.milkyway.core.executors.KubernetesExecutorService;
import dev.sushigumi.milkyway.endpoints.v1.api.TestPlanQueueRequest;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.bson.types.ObjectId;

@Path("/api/v1/queue")
public class TestQueueResource {
  private final KubernetesExecutorService executorService;
  private final TestPlanRepository testPlanRepository;

  public TestQueueResource(
      KubernetesExecutorService executorService, TestPlanRepository testPlanRepository) {
    this.executorService = executorService;
    this.testPlanRepository = testPlanRepository;
  }

  @Path("/{planId}")
  @POST
  public void queueTestPlan(@PathParam("planId") ObjectId planId, TestPlanQueueRequest request) {
    final TestPlan testPlan = testPlanRepository.findById(planId);
    if (testPlan == null) {
      throw new NotFoundException();
    }

    executorService.queueTest(null);
  }
}
