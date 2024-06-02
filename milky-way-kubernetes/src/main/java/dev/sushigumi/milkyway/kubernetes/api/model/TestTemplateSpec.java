package dev.sushigumi.milkyway.kubernetes.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.fabric8.generator.annotation.Required;
import io.fabric8.kubernetes.api.model.*;
import java.io.Serializable;

public class TestTemplateSpec implements Serializable {
  @Required private final String group;
  @Required private final Container container;
  @Required private final String commitHash;

  public TestTemplateSpec(
      @JsonProperty("group") String group,
      @JsonProperty("container") Container container,
      @JsonProperty("commitHash") String commitHash) {
    this.group = group;
    this.container = container;
    this.commitHash = commitHash;
  }

  public String getGroup() {
    return group;
  }

  public Container getContainer() {
    return container;
  }

  public String getCommitHash() {
    return commitHash;
  }
}
