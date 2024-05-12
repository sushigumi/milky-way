package dev.sushigumi.milkyway;

import dev.sushigumi.milkyway.kubernetes.api.model.TestTemplate;
import io.fabric8.kubernetes.api.model.apiextensions.v1.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;

public class TestUtils {
  public static final String TEST_TEMPLATES_RESOURCE =
      "META-INF/fabric8/testtemplates.sushigumi.dev-v1.yml";

  public static void setupCustomResourceDefinitions(KubernetesClient client) {
    CustomResourceDefinition crd =
        client
            .apiextensions()
            .v1()
            .customResourceDefinitions()
            .load(TestUtils.class.getClassLoader().getResourceAsStream(TEST_TEMPLATES_RESOURCE))
            .item();
    var resource = client.apiextensions().v1().customResourceDefinitions().resource(crd);
    resource.delete();
    resource.create();
  }

  public static void createTestTemplateCustomResource(KubernetesClient client, String filename) {
    final var op = client.resources(TestTemplate.class);
    final var testGroup =
        op.load(TestUtils.class.getClassLoader().getResourceAsStream(filename)).item();
    final var resource = op.resource(testGroup);
    resource.delete();
    resource.create();
  }

  public static void removeAllCustomResources(KubernetesClient client) {
    final var op = client.resources(TestTemplate.class);
    op.list().getItems().forEach(item -> op.resource(item).delete());
  }
}
