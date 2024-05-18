package dev.sushigumi.milkyway.endpoints.v1.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TestExecuteRequest {
  private final String testPlanId;

  @JsonCreator
  public TestExecuteRequest(@JsonProperty("testPlanId") String testPlanId) {
    this.testPlanId = testPlanId;
  }

  public String getTestPlanId() {
    return testPlanId;
  }
}
