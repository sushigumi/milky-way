package dev.sushigumi.milkyway.kubernetes;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobWatcher implements Watcher<Job> {
  private static final Logger LOGGER = LoggerFactory.getLogger(JobWatcher.class);

  @Override
  public void eventReceived(Action action, Job job) {
    LOGGER.info("{}", action);
    LOGGER.info("{}, {}, {}, {}, {}, {}", job.getMetadata().getName(), job.getStatus().getSucceeded(), job.getStatus().getFailed(), job.getStatus().getActive(), job.getStatus().getReady(), job.getStatus().getActive());
  }

  @Override
  public void onClose(WatcherException e) {
    LOGGER.error("Kubernetes job watcher closed", e);
  }
}
