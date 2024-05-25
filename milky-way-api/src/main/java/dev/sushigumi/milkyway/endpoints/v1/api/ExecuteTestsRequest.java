package dev.sushigumi.milkyway.endpoints.v1.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ExecuteTestsRequest {
  private final List<String> testIds;

  @JsonCreator
  public ExecuteTestsRequest(@JsonProperty("testIds") List<String> testIds) {
    this.testIds = testIds;
  }

  public List<String> getTestIds() {
    return testIds;
  }
}
