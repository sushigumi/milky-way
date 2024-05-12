package dev.sushigumi.milkyway.kubernetes.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.fabric8.kubernetes.api.model.*;
import java.io.Serializable;

public class TestTemplateSpec implements Serializable {
  private final String group;
  private final Container container;

  public TestTemplateSpec(
      @JsonProperty("group") String group, @JsonProperty("container") Container container) {
    this.group = group;
    this.container = container;
  }

  public String getGroup() {
    return group;
  }

  public Container getContainer() {
    return container;
  }
}
