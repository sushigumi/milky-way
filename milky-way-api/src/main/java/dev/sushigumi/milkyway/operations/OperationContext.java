package dev.sushigumi.milkyway.operations;

import dev.sushigumi.milkyway.database.TestPlanConfigurationRepository;
import dev.sushigumi.milkyway.database.TestPlanRepository;
import dev.sushigumi.milkyway.database.TestRepository;
import io.fabric8.kubernetes.client.KubernetesClient;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OperationContext {
  private final TestRepository testRepository;
  private final TestPlanRepository testPlanRepository;
  private final TestPlanConfigurationRepository testPlanConfigurationRepository;
  private final KubernetesClient k8sClient;

  public OperationContext(
      TestRepository testRepository,
      TestPlanRepository testPlanRepository,
      TestPlanConfigurationRepository testPlanConfigurationRepository,
      KubernetesClient k8sClient) {
    this.testRepository = testRepository;
    this.testPlanRepository = testPlanRepository;
    this.testPlanConfigurationRepository = testPlanConfigurationRepository;
    this.k8sClient = k8sClient;
  }

  public TestRepository getTestRepository() {
    return testRepository;
  }

  public TestPlanRepository getTestPlanRepository() {
    return testPlanRepository;
  }

  public TestPlanConfigurationRepository getTestPlanConfigurationRepository() {
    return testPlanConfigurationRepository;
  }

  public KubernetesClient getK8sClient() {
    return k8sClient;
  }
}
