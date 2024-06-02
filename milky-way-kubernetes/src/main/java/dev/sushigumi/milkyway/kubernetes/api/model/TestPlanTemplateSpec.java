package dev.sushigumi.milkyway.kubernetes.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.fabric8.generator.annotation.Required;
import java.io.Serializable;

public class TestPlanTemplateSpec implements Serializable {
  private final String description;
  @Required private final String[] testTemplates;
  @Required private final String commitHash;

  public TestPlanTemplateSpec(
      @JsonProperty("description") String description,
      @JsonProperty("testTemplates") String[] testTemplates,
      @JsonProperty("commitHash") String commitHash) {
    this.description = description;
    this.testTemplates = testTemplates;
    this.commitHash = commitHash;
  }

  public String getDescription() {
    return description;
  }

  public String[] getTestTemplates() {
    return testTemplates;
  }

  public String getCommitHash() {
    return commitHash;
  }
}
