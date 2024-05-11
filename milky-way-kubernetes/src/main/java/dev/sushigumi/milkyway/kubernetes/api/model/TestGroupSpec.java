package dev.sushigumi.milkyway.kubernetes.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class TestGroupSpec implements Serializable {
  private final TestSpec[] tests;

  @JsonCreator
  public TestGroupSpec(@JsonProperty("tests") TestSpec[] tests) {
    this.tests = tests;
  }

  public TestSpec[] getTests() {
    return tests;
  }
}
