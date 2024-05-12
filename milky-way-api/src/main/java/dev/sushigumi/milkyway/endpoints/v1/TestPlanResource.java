package dev.sushigumi.milkyway.endpoints.v1;

import dev.sushigumi.milkyway.KubernetesService;
import dev.sushigumi.milkyway.database.TestPlanRepository;
import dev.sushigumi.milkyway.database.entities.Test;
import dev.sushigumi.milkyway.database.entities.TestPlan;
import dev.sushigumi.milkyway.database.entities.TestStatus;
import dev.sushigumi.milkyway.database.projections.TestPlanSummary;
import dev.sushigumi.milkyway.endpoints.v1.api.CreateTestPlanRequest;
import dev.sushigumi.milkyway.kubernetes.api.model.TestTemplate;
import jakarta.ws.rs.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;

@Path("/api/v1/plans")
class TestPlanResource {
  private final TestPlanRepository testPlanRepository;
  private final KubernetesService kubernetesService;

  public TestPlanResource(
      TestPlanRepository testPlanRepository, KubernetesService kubernetesService) {
    this.testPlanRepository = testPlanRepository;
    this.kubernetesService = kubernetesService;
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
  public TestPlan createTestPlan(CreateTestPlanRequest request) {
    // Get all the test templates so that we can populate them
    final List<TestTemplate> testTemplates = kubernetesService.getTestTemplates();

    // Validate that the baseline env vars contains exactly the same properties as the candidate env
    // vars.
    if (!request.getBaselineEnvVars().keySet().equals(request.getCandidateEnvVars().keySet())) {
      throw new BadRequestException("Env vars do not match.");
    }

    // Validate that all the required fields are present.
    final Set<String> requiredEnvVars =
        testTemplates.stream()
            .map(template -> template.getSpec().getRequiredEnvVars())
            .filter(Objects::nonNull)
            .flatMap(Arrays::stream)
            .collect(Collectors.toUnmodifiableSet());
    if (!requiredEnvVars.equals(request.getBaselineEnvVars().keySet())) {
      throw new BadRequestException("Required env vars are not present.");
    }

    final var testPlan = new TestPlan();
    testPlan.name = request.getName();

    // Create a list of tests from the templates
    testPlan.tests =
        testTemplates.stream()
            .map(
                template -> {
                  final var test = new Test();
                  test.status = TestStatus.PENDING;
                  test.name = template.getMetadata().getName();
                  test.group = template.getSpec().getGroup();

                  return test;
                })
            .toList();

    testPlan.baselineEnvVars = request.getBaselineEnvVars();
    testPlan.candidateEnvVars = request.getCandidateEnvVars();

    try {
      testPlanRepository.persist(testPlan);
    } catch (Exception e) {
      throw new BadRequestException("Unable to create new test plan.", e);
    }

    return testPlan;
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
