package dev.sushigumi.milkyway.endpoints.v1;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sushigumi.milkyway.TestUtils;
import dev.sushigumi.milkyway.database.TestPlanRepository;
import dev.sushigumi.milkyway.database.entities.TestPlan;
import dev.sushigumi.milkyway.database.projections.TestPlanSummary;
import dev.sushigumi.milkyway.endpoints.v1.api.TestPlanCreateRequest;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithKubernetesTestServer
@QuarkusTest
@TestHTTPEndpoint(TestPlanResource.class)
class TestPlanResourceTest {
  private static final TestPlan DEFAULT_TEST_PLAN = createDefaultTestPlan();
  private final TestPlanRepository testPlanRepository;
  private final ObjectMapper objectMapper;
  private final KubernetesClient kubernetesClient;

  public TestPlanResourceTest(
      TestPlanRepository testPlanRepository,
      ObjectMapper objectmapper,
      KubernetesClient kubernetesClient) {
    this.testPlanRepository = testPlanRepository;
    this.objectMapper = objectmapper;
    this.kubernetesClient = kubernetesClient;
  }

  @BeforeEach
  void setup() {
    // Initialize the database with some dummy data.
    testPlanRepository.persistOrUpdate(DEFAULT_TEST_PLAN);
    TestUtils.setupCustomResourceDefinitions(kubernetesClient);
    TestUtils.removeAllCustomResources(kubernetesClient);
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
    testPlan2.baselineEnvVars = new HashMap<>();
    testPlan2.candidateEnvVars = new HashMap<>();
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
    TestUtils.createTestTemplateCustomResource(kubernetesClient, "test-template.yaml");
    final var request = new TestPlanCreateRequest("new test", new HashMap<>(), new HashMap<>());
    final String body = objectMapper.writeValueAsString(request);

    Response response =
        given()
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .post()
            .then()
            .statusCode(200)
            .extract()
            .response();
    String testPlanId = response.path("id");

    final TestPlan testPlan = testPlanRepository.findById(new ObjectId(testPlanId));
    assertEquals("new test", testPlan.name);
    assertEquals(1, testPlan.tests.size());
    assertEquals("dummy-test-job", testPlan.tests.getFirst().name);
    assertEquals("group1", testPlan.tests.getFirst().group);

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
    testPlan.baselineEnvVars = new HashMap<>();
    testPlan.candidateEnvVars = new HashMap<>();

    return testPlan;
  }
}
