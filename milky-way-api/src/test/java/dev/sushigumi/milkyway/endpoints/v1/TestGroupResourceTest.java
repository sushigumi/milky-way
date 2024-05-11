package dev.sushigumi.milkyway.endpoints.v1;

import static io.restassured.RestAssured.when;

import dev.sushigumi.milkyway.kubernetes.api.model.TestGroup;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.apiextensions.v1.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithKubernetesTestServer
@QuarkusTest
@TestHTTPEndpoint(TestGroupResource.class)
class TestGroupResourceTest {
  private final KubernetesClient kubernetesClient;
  private MixedOperation<TestGroup, KubernetesResourceList<TestGroup>, Resource<TestGroup>>
      testGroupClient;

  public TestGroupResourceTest(KubernetesClient kubernetesClient) {
    this.kubernetesClient = kubernetesClient;
  }

  @BeforeEach
  void init() {
    CustomResourceDefinition crd =
        kubernetesClient
            .apiextensions()
            .v1()
            .customResourceDefinitions()
            .load(
                getClass()
                    .getClassLoader()
                    .getResourceAsStream("META-INF/fabric8/testgroups.sushigumi.dev-v1.yml"))
            .item();
    var resource = kubernetesClient.apiextensions().v1().customResourceDefinitions().resource(crd);
    resource.delete();
    resource.create();

    testGroupClient = kubernetesClient.resources(TestGroup.class);
    testGroupClient.inNamespace("asdf").delete();
  }

  @Test
  void shouldGetTestPlan() {
    final var testGroup =
        testGroupClient
            .load(getClass().getClassLoader().getResourceAsStream("test-plan.yaml"))
            .item();

    testGroupClient.resource(testGroup).create();
    when().get("/dummy-test-group").then().assertThat().statusCode(200);
  }

  @Test
  void shouldThrowWhenTestPlanDoesNotExist() {
    when().get("/dummy-test-group").then().assertThat().statusCode(404);
  }
}
