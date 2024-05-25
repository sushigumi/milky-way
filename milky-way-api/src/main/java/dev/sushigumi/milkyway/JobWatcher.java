package dev.sushigumi.milkyway;

import dev.sushigumi.milkyway.database.entities.TestStatus;
import dev.sushigumi.milkyway.services.TestService;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobWatcher implements Watcher<Job> {
  private static final Logger LOGGER = LoggerFactory.getLogger(JobWatcher.class);

  private final TestService testService;

  public JobWatcher(TestService testService) {
    this.testService = testService;
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
    final String testId = metadata.getAnnotations().get("testId");
    LOGGER.info(
        "Job {} for test plan {} has completed with {} failed pods and {} succeeded pods.",
        job.getMetadata().getName(),
        testId,
        failedPods,
        succeededPods);

    // Update the status of the test.
    if (failedPods > 0) {
      testService.updateTestStatus(testId, TestStatus.FAILED);
    } else {
      testService.updateTestStatus(testId, TestStatus.SUCCESS);
    }
  }

  @Override
  public void onClose(WatcherException e) {
    LOGGER.error("Kubernetes job watcher closed", e);
  }
}
