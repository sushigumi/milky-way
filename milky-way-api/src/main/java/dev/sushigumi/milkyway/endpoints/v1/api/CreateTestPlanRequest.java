package dev.sushigumi.milkyway.endpoints.v1.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateTestPlanRequest {
  private final String name;

  @JsonCreator
  public CreateTestPlanRequest(@JsonProperty("name") String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
