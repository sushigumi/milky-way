package dev.sushigumi.milkyway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.sushigumi.milkyway.kubernetes.api.model.TestTemplate;
import io.fabric8.kubernetes.api.model.apiextensions.v1.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import org.junit.jupiter.api.Test;

@EnableKubernetesMockClient(crud = true)
public class TestTemplateCustomResourceTest {
  private KubernetesClient client;
  private KubernetesMockServer server;

  @Test
  void shouldCreateTestJobCustomResource() {
    CustomResourceDefinition crd =
        client
            .apiextensions()
            .v1()
            .customResourceDefinitions()
            .load(
                getClass()
                    .getClassLoader()
                    .getResourceAsStream("META-INF/fabric8/testtemplates.sushigumi.dev-v1.yml"))
            .item();
    var resource = client.apiextensions().v1().customResourceDefinitions().resource(crd);
    resource.delete();
    resource.create();

    final var testJobOp = client.resources(TestTemplate.class);
    final var testJob =
        testJobOp
            .load(getClass().getClassLoader().getResourceAsStream("test-template.yaml"))
            .item();
    final var testJobCrd = testJobOp.resource(testJob).create();
    assertNotNull(testJobOp.withName("dummy-test-job").get());
    assertEquals(1, testJobOp.list().getItems().size());
  }
}
