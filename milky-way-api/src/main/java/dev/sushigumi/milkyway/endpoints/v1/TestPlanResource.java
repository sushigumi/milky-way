package dev.sushigumi.milkyway.endpoints.v1;

import dev.sushigumi.milkyway.core.database.TestPlanRepository;
import dev.sushigumi.milkyway.core.database.entities.TestPlan;
import dev.sushigumi.milkyway.core.database.projections.TestPlanSummary;
import dev.sushigumi.milkyway.endpoints.v1.api.CreateTestPlanRequest;
import jakarta.ws.rs.*;
import java.util.List;
import org.bson.types.ObjectId;

@Path("/api/v1/plans")
class TestPlanResource {
  private final TestPlanRepository testPlanRepository;

  public TestPlanResource(TestPlanRepository testPlanRepository) {
    this.testPlanRepository = testPlanRepository;
  }

  @Path("/")
  @GET
  public List<TestPlanSummary> getTestPlanSummaries() {
    return testPlanRepository.getAllTestPlanSummaries();
  }

  @Path("/{planId}")
  @GET
  public TestPlan getTestPlan(@PathParam("planId") ObjectId planId) {
    final TestPlan testPlan = testPlanRepository.findById(planId);
    if (testPlan == null) {
      throw new NotFoundException();
    }

    return testPlan;
  }

  @Path("/")
  @POST
  public void createTestPlan(CreateTestPlanRequest request) {
    final var testPlan = new TestPlan();
    testPlan.name = request.getName();

    try {
      testPlanRepository.persist(testPlan);
    } catch (Exception e) {
      throw new BadRequestException("Unable to create new test plan.", e);
    }
  }

  @Path("/{planId}")
  @DELETE
  public void deleteTestPlan(@PathParam("planId") ObjectId planId) {
    boolean success = testPlanRepository.deleteById(planId);
    if (!success) {
      throw new BadRequestException(
          "Unable to delete the test plan. An error occurred or the test plan doesn't exist.");
    }
  }
}
