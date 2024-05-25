package dev.sushigumi.milkyway.endpoints.v1;

import dev.sushigumi.milkyway.database.entities.TestPlan;
import dev.sushigumi.milkyway.database.projections.TestPlanSummary;
import dev.sushigumi.milkyway.endpoints.v1.api.TestPlanCreateRequest;
import dev.sushigumi.milkyway.kubernetes.api.model.TestTemplate;
import dev.sushigumi.milkyway.services.TestService;
import dev.sushigumi.milkyway.services.TestTemplateService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/api/v1/plans")
class TestPlanResource {
  private final TestService testService;
  private final TestTemplateService testTemplateService;

  public TestPlanResource(TestService testService, TestTemplateService testTemplateService) {
    this.testService = testService;
    this.testTemplateService = testTemplateService;
  }

  @Path("/")
  @GET
  public List<TestPlanSummary> getTestPlanSummaries() {
    return testService.getAllTestPlanSummaries();
  }

  @Path("/{planId}")
  @GET
  public TestPlan getTestPlan(@PathParam("planId") String planId) {
    final TestPlan testPlan = testService.getTestPlanById(planId);
    if (testPlan == null) {
      throw new NotFoundException();
    }

    return testPlan;
  }

  @Path("/")
  @POST
  public TestPlan createTestPlan(@Valid TestPlanCreateRequest request) {
    // Get all the test templates so that we can populate them
    final List<TestTemplate> testTemplates = testTemplateService.getTestTemplates();

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

    return testService.createTestPlan(
        request.getConfigurationId(),
        request.getName(),
        request.getBaselineEnvVars(),
        request.getCandidateEnvVars());
  }

  @Path("/{planId}")
  @DELETE
  public void deleteTestPlan(@PathParam("planId") String planId) {
    boolean success = testService.deleteTestPlanById(planId);
    if (!success) {
      throw new BadRequestException(
          "Unable to delete the test plan. An error occurred or the test plan doesn't exist.");
    }
  }
}
