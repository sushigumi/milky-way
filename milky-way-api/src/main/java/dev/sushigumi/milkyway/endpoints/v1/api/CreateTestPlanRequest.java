package dev.sushigumi.milkyway.endpoints.v1.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.sushigumi.milkyway.operations.create.CreateTestPlanOperation;

public class CreateTestPlanRequest {
  private final String template;
  private final String name;

  @JsonCreator
  public CreateTestPlanRequest(
      @JsonProperty("template") String template, @JsonProperty("name") String name) {
    this.template = template;
    this.name = name;
  }

  public String getTemplate() {
    return template;
  }

  public String getName() {
    return name;
  }

  public CreateTestPlanOperation toOperation() {
    return new CreateTestPlanOperation(template, name);
  }
}
