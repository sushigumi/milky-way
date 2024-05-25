package dev.sushigumi.milkyway.endpoints.v1.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public class TestPlanCreateRequest {
  private final String configurationId;
  private final String name;
  @NotNull private final Map<String, String> baselineEnvVars;
  @NotNull private final Map<String, String> candidateEnvVars;

  @JsonCreator
  public TestPlanCreateRequest(
      @JsonProperty("configurationId") String configurationId,
      @JsonProperty("name") String name,
      @JsonProperty("baselineEnvVars") Map<String, String> baselineEnvVars,
      @JsonProperty("candidateEnvVars") Map<String, String> candidateEnvVars) {
    this.configurationId = configurationId;
    this.name = name;
    this.baselineEnvVars = baselineEnvVars;
    this.candidateEnvVars = candidateEnvVars;
  }

  public String getConfigurationId() {
    return configurationId;
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
