package dev.sushigumi.milkyway.operations;

import dev.sushigumi.milkyway.database.TestPlanRepository;
import dev.sushigumi.milkyway.database.TestRepository;
import dev.sushigumi.milkyway.services.CrdTemplateService;
import dev.sushigumi.milkyway.services.JobService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OperationContext {
  private final TestRepository testRepository;
  private final TestPlanRepository testPlanRepository;
  private final CrdTemplateService crdTemplateService;
  private final JobService jobService;

  public OperationContext(
      TestRepository testRepository,
      TestPlanRepository testPlanRepository,
      CrdTemplateService crdTemplateService,
      JobService jobService) {
    this.testRepository = testRepository;
    this.testPlanRepository = testPlanRepository;
    this.crdTemplateService = crdTemplateService;
    this.jobService = jobService;
  }

  public TestRepository getTestRepository() {
    return testRepository;
  }

  public TestPlanRepository getTestPlanRepository() {
    return testPlanRepository;
  }

  public CrdTemplateService getCrdTemplateService() {
    return crdTemplateService;
  }

  public JobService getJobService() {
    return jobService;
  }
}
