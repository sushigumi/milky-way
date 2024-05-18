package dev.sushigumi.milkyway;

import dev.sushigumi.milkyway.kubernetes.api.model.TestTemplate;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.Serialization;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TestTemplateService {
  private final KubernetesClient client;

  public TestTemplateService(KubernetesClient client) {
    this.client = client;
  }

  public String getTestTemplateAsYaml(String name) {
    final var resource = client.resources(TestTemplate.class).withName(name).get();
    return resource == null ? null : Serialization.asYaml(resource);
  }

  public List<TestTemplate> getTestTemplates() {
    return client.resources(TestTemplate.class).list().getItems();
  }
}
