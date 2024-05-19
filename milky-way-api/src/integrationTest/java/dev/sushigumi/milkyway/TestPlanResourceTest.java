package dev.sushigumi.milkyway;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sushigumi.milkyway.endpoints.v1.api.TestPlanCreateRequest;
import dev.sushigumi.milkyway.lifecycle.KubernetesTestResourceManager;
import dev.sushigumi.milkyway.lifecycle.MongoTestResourceManager;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
@QuarkusTestResource(MongoTestResourceManager.class)
@QuarkusTestResource(KubernetesTestResourceManager.class)
public class TestPlanResourceTest {
  @Test
  void getTestPlans() {
    Response response =
        when()
            .get("/api/v1/plans")
            .then()
            .assertThat()
            .statusCode(200)
            .body("size()", is(1))
            .body("[0].name", equalTo("test plan 1"))
            .extract()
            .response();

    String testPlanId1 = response.path("[0].id");
    when()
        .get("/api/v1/plans/" + testPlanId1)
        .then()
        .assertThat()
        .statusCode(200)
        .body("name", equalTo("test plan 1"))
        .body("tests.size()", is(1));
  }

  @Test
  void createAndDeleteTestPlans() throws JsonProcessingException {
    // Create a new test plan
    final var request =
        new TestPlanCreateRequest("test plan new", new HashMap<>(), new HashMap<>());
    final String body = new ObjectMapper().writeValueAsString(request);
    Response response =
        given()
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .post("/api/v1/plans/")
            .then()
            .statusCode(200)
            .extract()
            .response();
    String testPlanId = response.path("id");

    // Check that the test plan was created successfully
    when().get("/api/v1/plans/" + testPlanId).then().assertThat().statusCode(200);

    // Delete the test plan. The second call will fail because it doesn't exist anymore.
    when().delete("/api/v1/plans/" + testPlanId).then().assertThat().statusCode(204);
    when().delete("/api/v1/plans/" + testPlanId).then().assertThat().statusCode(400);
  }
}
