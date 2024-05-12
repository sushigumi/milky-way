package dev.sushigumi.milkyway.endpoints.v1;

import static io.restassured.RestAssured.when;

import dev.sushigumi.milkyway.TestUtils;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithKubernetesTestServer
@QuarkusTest
@TestHTTPEndpoint(TestTemplateResource.class)
class TestTemplateResourceTest {
  private final KubernetesClient kubernetesClient;

  public TestTemplateResourceTest(KubernetesClient kubernetesClient) {
    this.kubernetesClient = kubernetesClient;
  }

  @BeforeEach
  void init() {
    TestUtils.setupCustomResourceDefinitions(kubernetesClient);
    TestUtils.removeAllCustomResources(kubernetesClient);
  }

  @Test
  void shouldGetTestPlan() {
    TestUtils.createTestTemplateCustomResource(kubernetesClient, "test-template.yaml");
    when().get("/dummy-test-job").then().assertThat().statusCode(200);
  }

  @Test
  void shouldThrowWhenTestPlanDoesNotExist() {
    when().get("/dummy-test-job").then().assertThat().statusCode(404);
  }
}
