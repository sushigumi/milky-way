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
public class CrdTemplatesTest {
  @Test
  void testTemplateExists() {
    when().get("/api/v1/templates/tests/test-template-1").then().assertThat().statusCode(200);
  }

  @Test
  void testTemplateDoesNotExist() {
    when().get("/api/v1/templates/tests/unknown").then().assertThat().statusCode(404);
  }

  @Test
  void testPlanTemplateExists() {
    when().get("/api/v1/templates/plans/test-plan-template-1").then().assertThat().statusCode(200);
  }

  @Test
  void testPlanTemplateDoesNotExist() {
    when().get("/api/v1/templates/plans/unknown").then().assertThat().statusCode(404);
  }
}
