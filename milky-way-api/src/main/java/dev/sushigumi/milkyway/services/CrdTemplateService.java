package dev.sushigumi.milkyway.services;

import dev.sushigumi.milkyway.kubernetes.api.model.TestPlanTemplate;
import dev.sushigumi.milkyway.kubernetes.api.model.TestTemplate;
import io.fabric8.kubernetes.client.KubernetesClient;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CrdTemplateService {
  private final KubernetesClient k8sClient;

  public CrdTemplateService(KubernetesClient k8sClient) {
    this.k8sClient = k8sClient;
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
}
