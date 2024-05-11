package dev.sushigumi.milkyway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.sushigumi.milkyway.kubernetes.api.model.TestGroup;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.apiextensions.v1.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@EnableKubernetesMockClient(crud = true)
public class TestGroupCustomResourceTest {
  private KubernetesClient client;
  private KubernetesMockServer server;
  private MixedOperation<TestGroup, KubernetesResourceList<TestGroup>, Resource<TestGroup>>
      testGroupClient;

  @BeforeEach
  void init() {
    CustomResourceDefinition crd =
        client
            .apiextensions()
            .v1()
            .customResourceDefinitions()
            .load(
                TestGroupCustomResourceTest.class
                    .getClassLoader()
                    .getResourceAsStream("META-INF/fabric8/testgroups.sushigumi.dev-v1.yml"))
            .item();
    var resource = client.apiextensions().v1().customResourceDefinitions().resource(crd);
    resource.delete();
    resource.create();

    testGroupClient = client.resources(TestGroup.class);
    testGroupClient.delete();
  }

  @Test
  void shouldApplyAndReadTestGroupCustomResource() {
    final var testGroup =
        testGroupClient
            .load(getClass().getClassLoader().getResourceAsStream("testgroup.yaml"))
            .item();
    TestGroup testGroupCrd = testGroupClient.resource(testGroup).create();
    assertNotNull(testGroupClient.withName("dummy-test-group"));
    assertEquals(1, testGroupClient.list().getItems().size());
  }
}
