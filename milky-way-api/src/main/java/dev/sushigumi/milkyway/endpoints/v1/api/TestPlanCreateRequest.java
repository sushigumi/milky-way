package dev.sushigumi.milkyway.endpoints.v1.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TestPlanCreateRequest {
  private final String configurationId;
  private final String name;

  @JsonCreator
  public TestPlanCreateRequest(
      @JsonProperty("configurationId") String configurationId, @JsonProperty("name") String name) {
    this.configurationId = configurationId;
    this.name = name;
  }

  public String getConfigurationId() {
    return configurationId;
  }

  public String getName() {
    return name;
  }
}
