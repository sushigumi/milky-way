package dev.sushigumi.milkyway;

import static io.restassured.RestAssured.when;

import dev.sushigumi.milkyway.lifecycle.KubernetesTestResourceManager;
import dev.sushigumi.milkyway.lifecycle.MongoTestResourceManager;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
@QuarkusTestResource(MongoTestResourceManager.class)
@QuarkusTestResource(KubernetesTestResourceManager.class)
public class TestTemplateResourceTest {
  @Test
  void templateExists() {
    when().get("/api/v1/templates/dummy-test-job").then().assertThat().statusCode(200);
  }

  @Test
  void templateDoesNotExist() {
    when().get("/api/v1/templates/unknown-job").then().assertThat().statusCode(404);
  }
}
