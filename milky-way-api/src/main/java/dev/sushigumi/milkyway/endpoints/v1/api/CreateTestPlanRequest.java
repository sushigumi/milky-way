package dev.sushigumi.milkyway.endpoints.v1.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.sushigumi.milkyway.operations.create.CreateTestPlanOperation;

public class CreateTestPlanRequest {
  private final String configurationId;
  private final String name;

  @JsonCreator
  public CreateTestPlanRequest(
      @JsonProperty("configurationId") String configurationId, @JsonProperty("name") String name) {
    this.configurationId = configurationId;
    this.name = name;
  }

  public CreateTestPlanOperation toOperation() {
    return new CreateTestPlanOperation(configurationId, name);
  }
}
