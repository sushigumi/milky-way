package dev.sushigumi.milkyway;

import dev.sushigumi.milkyway.database.entities.TestStatus;
import dev.sushigumi.milkyway.exceptions.TestStatusUpdateException;
import dev.sushigumi.milkyway.services.TestPlanService;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobWatcher implements Watcher<Job> {
  private static final Logger LOGGER = LoggerFactory.getLogger(JobWatcher.class);

  private final TestPlanService testPlanService;

  public JobWatcher(TestPlanService testPlanService) {
    this.testPlanService = testPlanService;
  }

  private int getPodCount(Integer value) {
    return value == null ? 0 : value;
  }

  @Override
  public void eventReceived(Action action, Job job) {
    // Once there are no more active pods left, then we can update the status
    final JobStatus jobStatus = job.getStatus();

    // If a job doesn't have a completion time, it hasn't completed.
    final String completionTime = jobStatus.getCompletionTime();
    if (completionTime == null) {
      return;
    }

    final int failedPods = getPodCount(jobStatus.getFailed());
    final int succeededPods = getPodCount(jobStatus.getSucceeded());
    final ObjectMeta metadata = job.getMetadata();
    final String testPlanId = metadata.getAnnotations().get("testPlanId");
    final String testName = metadata.getAnnotations().get("testsName");
    LOGGER.info(
        "Job {} for test plan {} has completed with {} failed pods and {} succeeded pods.",
        job.getMetadata().getName(),
        testPlanId,
        failedPods,
        succeededPods);

    // Update the status of the test.
    try {
      if (failedPods > 0) {
        testPlanService.updateTestStatus(testPlanId, testName, TestStatus.FAILED);
      } else {
        testPlanService.updateTestStatus(testPlanId, testName, TestStatus.SUCCESS);
      }
    } catch (TestStatusUpdateException e) {
      LOGGER.error("An error occurred", e);
    }
  }

  @Override
  public void onClose(WatcherException e) {
    LOGGER.error("Kubernetes job watcher closed", e);
  }
}
