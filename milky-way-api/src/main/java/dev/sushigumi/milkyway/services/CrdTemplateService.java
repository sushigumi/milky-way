package dev.sushigumi.milkyway.services;

import dev.sushigumi.milkyway.kubernetes.api.model.TestPlanTemplate;
import dev.sushigumi.milkyway.kubernetes.api.model.TestTemplate;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class CrdTemplateService {
  private static final Logger LOGGER = LoggerFactory.getLogger(CrdTemplateService.class);

  private final KubernetesClient k8sClient;
  private final ConcurrentMap<String, String> testTemplateCommitHashMap = new ConcurrentHashMap<>();
  private Watch testTemplateWatch;

  public CrdTemplateService(KubernetesClient k8sClient) {
    this.k8sClient = k8sClient;
  }

  void startup(@Observes StartupEvent event) {
    final var resource = k8sClient.resources(TestTemplate.class);
    for (var testTemplate : resource.list().getItems()) {
      testTemplateCommitHashMap.put(
          testTemplate.getMetadata().getName(), testTemplate.getSpec().getCommitHash());
    }
  }

  @PostConstruct
  void init() {
    LOGGER.info("Registering kubernetes test template watcher.");
    testTemplateWatch =
        k8sClient
            .resources(TestTemplate.class)
            .watch(new TestTemplateWatcher(testTemplateCommitHashMap));
  }

  @PreDestroy
  void destroy() {
    if (testTemplateWatch != null) {
      LOGGER.info("Closing kubernetes test template watcher.");
      testTemplateWatch.close();
      testTemplateWatch = null;
    }
  }

  public TestTemplate getTestTemplateByName(String name) {
    return k8sClient.resources(TestTemplate.class).withName(name).get();
  }

  public TestPlanTemplate getTestPlanTemplateByName(String name) {
    return k8sClient.resources(TestPlanTemplate.class).withName(name).get();
  }

  public List<TestPlanTemplate> getAllTestPlanTemplates() {
    return k8sClient.resources(TestPlanTemplate.class).list().getItems();
  }

  public String getTestTemplateCommitHashByName(String name) {
    return testTemplateCommitHashMap.get(name);
  }

  private static class TestTemplateWatcher implements Watcher<TestTemplate> {
    private final ConcurrentMap<String, String> testTemplateCommitHashMap;

    public TestTemplateWatcher(ConcurrentMap<String, String> testTemplateCommitHashMap) {
      this.testTemplateCommitHashMap = testTemplateCommitHashMap;
    }

    @Override
    public void eventReceived(Action action, TestTemplate testTemplate) {
      this.testTemplateCommitHashMap.put(
          testTemplate.getMetadata().getName(), testTemplate.getSpec().getCommitHash());
    }

    @Override
    public void onClose(WatcherException e) {}
  }
}
