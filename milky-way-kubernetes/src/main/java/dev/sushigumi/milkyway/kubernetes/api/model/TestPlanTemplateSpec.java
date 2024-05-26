package dev.sushigumi.milkyway.kubernetes.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.fabric8.generator.annotation.Required;
import java.io.Serializable;

public class TestPlanTemplateSpec implements Serializable {
  private final String description;
  @Required private final String[] testTemplates;

  public TestPlanTemplateSpec(
      @JsonProperty("description") String description,
      @JsonProperty("testTemplates") String[] testTemplates) {
    this.description = description;
    this.testTemplates = testTemplates;
  }

  public String getDescription() {
    return description;
  }

  public String[] getTestTemplates() {
    return testTemplates;
  }
}
