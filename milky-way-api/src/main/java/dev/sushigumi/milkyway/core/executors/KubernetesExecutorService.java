package dev.sushigumi.milkyway.core.executors;

import dev.sushigumi.milkyway.core.ExecutorService;
import dev.sushigumi.milkyway.core.database.KubernetesTestConfigRepository;
import dev.sushigumi.milkyway.core.database.entities.KubernetesTestSpec;
import dev.sushigumi.milkyway.core.database.entities.Test;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@ApplicationScoped
public class KubernetesExecutorService implements ExecutorService {
  private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesExecutorService.class);

  private final KubernetesClient kubernetesClient;
  private final KubernetesTestConfigRepository configRepository;
  private final String namespace;
  private final JobWatcher jobWatcher = new JobWatcher();

  public KubernetesExecutorService(
      KubernetesClient kubernetesClient,
      KubernetesTestConfigRepository configRepository,
      @ConfigProperty(name = "namespace") String namespace) {
    this.kubernetesClient = kubernetesClient;
    this.configRepository = configRepository;
    this.namespace = namespace;
  }

  void startup(@Observes StartupEvent event) {
    if (kubernetesClient.namespaces().withName(namespace).get() == null) {
      LOGGER.info("Namespace {} doesn't exist. Attempting to create a new one.", namespace);
      Namespace ns =
          new NamespaceBuilder().withNewMetadata().withName(namespace).endMetadata().build();
      kubernetesClient.namespaces().resource(ns).create();
      LOGGER.info("Successfully created namespace {}.", namespace);
    }
  }

  @PostConstruct
  void init() {
    LOGGER.info("Registering kubernetes job watcher.");
    kubernetesClient.batch().v1().jobs().inNamespace(namespace).watch(new JobWatcher());
  }

  private KubernetesTestSpec getTestConfig(Test test) {
    return configRepository.findById(test.configId);
  }

  public void queueTest(Test test) {
    //    final KubernetesTestSpec testConfig = getTestConfig(test);
    final var job =
        kubernetesClient
            .batch()
            .v1()
            .jobs()
            .load(getClass().getResourceAsStream("/test-job.yaml"))
            .item();
    kubernetesClient.batch().v1().jobs().inNamespace(namespace).resource(job).create();
  }

  public static class JobWatcher implements Watcher<Job> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobWatcher.class);

    @Override
    public void eventReceived(Action action, Job job) {
      LOGGER.info("event received");
    }

    @Override
    public void onClose(WatcherException e) {
      LOGGER.info("on close");
    }
  }
}
