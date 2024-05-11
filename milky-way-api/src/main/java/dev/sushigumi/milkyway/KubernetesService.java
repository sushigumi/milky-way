package dev.sushigumi.milkyway;

import dev.sushigumi.milkyway.kubernetes.api.model.TestGroup;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.Serialization;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KubernetesService {
  private final KubernetesClient client;

  public KubernetesService(KubernetesClient client) {
    this.client = client;
  }

  public String getTestGroupResource(String name) {
    final var resource = client.resources(TestGroup.class).withName(name).get();
    return resource == null ? null : Serialization.asYaml(resource);
  }
}
