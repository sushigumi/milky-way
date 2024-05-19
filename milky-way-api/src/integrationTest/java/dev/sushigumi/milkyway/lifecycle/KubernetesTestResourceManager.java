package dev.sushigumi.milkyway.lifecycle;

import dev.sushigumi.milkyway.kubernetes.api.model.TestTemplate;
import io.fabric8.kubernetes.api.model.apiextensions.v1.CustomResourceDefinition;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.quarkus.test.common.DevServicesContext;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.io.InputStream;
import java.util.Map;

public class KubernetesTestResourceManager
    implements QuarkusTestResourceLifecycleManager, DevServicesContext.ContextAware {
  public static final String TEST_TEMPLATES_CRD =
      "META-INF/fabric8/testtemplates.sushigumi.dev-v1.yml";
  public static final String TEST_TEMPLATE = "test-templates/1.yaml";

  private String clientKeyAlgo;
  private String apiServerUrl;
  private String clientCertData;
  private String caCertData;
  private String clientKeyData;
  private String namespace;

  @Override
  public void setIntegrationTestContext(DevServicesContext context) {
    Map<String, String> properties = context.devServicesProperties();
    clientKeyAlgo = properties.get("quarkus.kubernetes-client.client-key-algo");
    apiServerUrl = properties.get("quarkus.kubernetes-client.api-server-url");
    clientCertData = properties.get("quarkus.kubernetes-client.client-cert-data");
    caCertData = properties.get("quarkus.kubernetes-client.ca-cert-data");
    clientKeyData = properties.get("quarkus.kubernetes-client.client-key-data");
    namespace = properties.get("quarkus.kubernetes-client.namespace");
  }

  private void createTestTemplate(KubernetesClient client, String path) {
    final var op = client.resources(TestTemplate.class);
    final var template = op.load(getClass().getClassLoader().getResourceAsStream(path)).item();
    final var resource = op.resource(template);
    resource.delete();
    resource.create();
  }

  private void initializeKubernetesCluster(KubernetesClient client) {
    // Add the CRD.
    InputStream stream = getClass().getClassLoader().getResourceAsStream(TEST_TEMPLATES_CRD);
    CustomResourceDefinition crd =
        client.apiextensions().v1().customResourceDefinitions().load(stream).item();
    var crdResource = client.apiextensions().v1().customResourceDefinitions().resource(crd);
    crdResource.delete();
    crdResource.create();

    // Add a dummy test template
    createTestTemplate(client, TEST_TEMPLATE);
  }

  @Override
  public Map<String, String> start() {
    Config kubernetesConfig =
        new ConfigBuilder()
            .withMasterUrl(apiServerUrl)
            .withClientKeyAlgo(clientKeyAlgo)
            .withClientCertData(clientCertData)
            .withCaCertData(caCertData)
            .withClientKeyData(clientKeyData)
            .withNamespace(namespace)
            .build();
    try (KubernetesClient kubernetesClient =
        new KubernetesClientBuilder().withConfig(kubernetesConfig).build()) {
      initializeKubernetesCluster(kubernetesClient);
    }

    return Map.of();
  }

  @Override
  public void stop() {}
}
