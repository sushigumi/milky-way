package dev.sushigumi.milkyway.services;

import dev.sushigumi.milkyway.database.TestPlanRepository;
import dev.sushigumi.milkyway.database.entities.TestPlan;
import dev.sushigumi.milkyway.database.entities.TestStatus;
import dev.sushigumi.milkyway.exceptions.TestStatusUpdateException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.bson.types.ObjectId;

@ApplicationScoped
public class TestPlanService {
  private final TestPlanRepository testPlanRepository;

  public TestPlanService(TestPlanRepository testPlanRepository) {
    this.testPlanRepository = testPlanRepository;
  }

  public TestPlan getTestPlanById(String testPlanId) {
    return testPlanRepository.findById(new ObjectId(testPlanId));
  }

  @Transactional
  public void updateTestStatus(String testPlanId, String testName, TestStatus testStatus)
      throws TestStatusUpdateException {
    final TestPlan testPlan = testPlanRepository.findById(new ObjectId(testPlanId));
    if (testPlan == null) {
      throw new TestStatusUpdateException("Test plan does not exist.");
    }

    // Update the status of the test. Only the submitter of the job should update the status.
    for (var test : testPlan.tests) {
      if (test.name.equals(testName)) {
        test.status = testStatus;
        break;
      }
    }

    // Update the distributed cache first so that other clients will know the updated status.

    // Update the database.
    testPlanRepository.update(testPlan);
  }
}
