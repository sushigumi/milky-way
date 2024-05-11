package dev.sushigumi.milkyway.endpoints.v1;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sushigumi.milkyway.core.database.TestPlanRepository;
import dev.sushigumi.milkyway.core.database.entities.TestPlan;
import dev.sushigumi.milkyway.core.database.projections.TestPlanSummary;
import dev.sushigumi.milkyway.endpoints.v1.api.CreateTestPlanRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(TestPlanResource.class)
class TestPlanResourceTest {
  private static final TestPlan DEFAULT_TEST_PLAN = createDefaultTestPlan();
  private final TestPlanRepository testPlanRepository;
  private final ObjectMapper objectMapper;

  public TestPlanResourceTest(TestPlanRepository testPlanRepository, ObjectMapper objectmapper) {
    this.testPlanRepository = testPlanRepository;
    this.objectMapper = objectmapper;
  }

  @BeforeEach
  void setup() {
    // Initialize the database with some dummy data.
    testPlanRepository.persistOrUpdate(DEFAULT_TEST_PLAN);
  }

  @AfterEach
  void teardown() {
    // Make sure we start fresh.
    testPlanRepository.deleteAll();
  }

  @Test
  void shouldGetTestPlanSummaries() {
    // Single test plan in database.
    when()
        .get()
        .then()
        .assertThat()
        .statusCode(200)
        .body("size()", is(1))
        .body("[0].name", equalTo("dummy test plan"));

    // Add a new test plan into the database.
    final var testPlan2 = new TestPlan();
    testPlan2.name = "dummy test plan 2";
    testPlan2.tests = new ArrayList<>();
    testPlan2.baselineProperties = new HashMap<>();
    testPlan2.candidateProperties = new HashMap<>();
    testPlanRepository.persistOrUpdate(testPlan2);

    when()
        .get()
        .then()
        .assertThat()
        .statusCode(200)
        .body("size()", is(2))
        .body("name", hasItems("dummy test plan", "dummy test plan 2"));

    // Delete the default test plan.
    testPlanRepository.delete(DEFAULT_TEST_PLAN);

    when()
        .get()
        .then()
        .assertThat()
        .statusCode(200)
        .body("size()", is(1))
        .body("[0].name", equalTo("dummy test plan 2"));

    // Delete all the test plans.
    testPlanRepository.deleteAll();

    when().get().then().assertThat().statusCode(200).body("size()", is(0));
  }

  @Test
  void shouldGetTestPlan() {
    when()
        .get(String.format("/%s", DEFAULT_TEST_PLAN.id.toString()))
        .then()
        .assertThat()
        .statusCode(200)
        .body("name", equalTo("dummy test plan"))
        .body("tests.size()", is(0));
  }

  @Test
  void shouldFailWhenGetTestPlanThatDoesNotExist() {
    when().get(String.format("/%s", new ObjectId())).then().assertThat().statusCode(404);
  }

  @Test
  void shouldCreateTestPlan() throws JsonProcessingException {
    final var request = new CreateTestPlanRequest("new test");
    final String body = objectMapper.writeValueAsString(request);

    given().contentType(ContentType.JSON).body(body).when().post().then().statusCode(204);

    final List<TestPlanSummary> summaries =
        testPlanRepository.getAllTestPlanSummariesByName("new test");
    assertEquals(1, summaries.size());

    final TestPlanSummary summary = summaries.getFirst();
    assertEquals("new test", summary.name);
  }

  @Test
  void shouldDeleteTestPlan() {
    when().delete("/" + DEFAULT_TEST_PLAN.id.toString()).then().assertThat().statusCode(204);

    assertEquals(0, testPlanRepository.getAllTestPlanSummaries().size());
  }

  @Test
  void shouldFailWhenDeletingTestPlanThatDoesNotExist() {
    when().delete("/" + new ObjectId()).then().assertThat().statusCode(400);

    assertEquals(1, testPlanRepository.getAllTestPlanSummaries().size());
  }

  private static TestPlan createDefaultTestPlan() {
    final var testPlan = new TestPlan();
    testPlan.name = "dummy test plan";
    testPlan.tests = new ArrayList<>();
    testPlan.baselineProperties = new HashMap<>();
    testPlan.candidateProperties = new HashMap<>();

    return testPlan;
  }
}
