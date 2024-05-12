package dev.sushigumi.milkyway.endpoints.v1.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class CreateTestPlanRequest {
  private final String name;
  private final Map<String, String> baselineEnvVars;
  private final Map<String, String> candidateEnvVars;

  @JsonCreator
  public CreateTestPlanRequest(
      @JsonProperty("name") String name,
      @JsonProperty("baselineProperties") Map<String, String> baselineEnvVars,
      @JsonProperty("candidateProperties") Map<String, String> candidateEnvVars) {
    this.name = name;
    this.baselineEnvVars = baselineEnvVars;
    this.candidateEnvVars = candidateEnvVars;
  }

  public String getName() {
    return name;
  }

  public Map<String, String> getBaselineEnvVars() {
    return baselineEnvVars;
  }

  public Map<String, String> getCandidateEnvVars() {
    return candidateEnvVars;
  }
}
