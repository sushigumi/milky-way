package dev.sushigumi.milkyway.services;

import dev.sushigumi.milkyway.database.entities.TestStatus;
import dev.sushigumi.milkyway.operations.update.UpdateTestStatusOperation;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class JobService {
  private static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);

  private final KubernetesClient k8sClient;
  private final OperationExecutorService executorService;
  private Watch jobWatch;

  public JobService(KubernetesClient k8sClient, OperationExecutorService executorService) {
    this.k8sClient = k8sClient;
    this.executorService = executorService;
  }

  @PostConstruct
  void init() {
    LOGGER.info("Registering kubernetes job watcher.");
    jobWatch = k8sClient.batch().v1().jobs().watch(new JobWatcher(executorService));
  }

  @PreDestroy
  void destroy() {
    if (jobWatch != null) {
      LOGGER.info("Closing kubernetes job watcher.");
      jobWatch.close();
      jobWatch = null;
    }
  }

  public void submitJob(Job job) {
    k8sClient.batch().v1().jobs().resource(job).create();
  }

  private static class JobWatcher implements Watcher<Job> {
    private final OperationExecutorService executorService;

    private JobWatcher(OperationExecutorService executorService) {
      this.executorService = executorService;
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
        executorService.execute(new UpdateTestStatusOperation(testId, TestStatus.FAILED));
      } else {
        executorService.execute(new UpdateTestStatusOperation(testId, TestStatus.SUCCESS));
      }
    }

    @Override
    public void onClose(WatcherException e) {
      LOGGER.error("Kubernetes job watcher closed", e);
    }
  }
}
