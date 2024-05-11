package dev.sushigumi.milkyway.kubernetes.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.fabric8.generator.annotation.Required;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;
import io.fabric8.kubernetes.api.model.batch.v1.PodFailurePolicy;

public class TestSpec {
  @Required private final String name;
  @Required private final String mainContainer;
  private final int backoffLimit;
  private final int ttlSecondsAfterFinished;
  private final PodFailurePolicy podFailurePolicy;
  private final PodTemplateSpec template;

  @JsonCreator
  public TestSpec(
      @JsonProperty("name") String name,
      @JsonProperty("mainContainer") String mainContainer,
      @JsonProperty("backoffLimit") int backoffLimit,
      @JsonProperty("ttlSecondsAfterFinished") int ttlSecondsAfterFinished,
      @JsonProperty("podFailurePolicy") PodFailurePolicy podFailurePolicy,
      @JsonProperty("container") PodTemplateSpec template) {
    this.name = name;
    this.mainContainer = mainContainer;
    this.backoffLimit = backoffLimit;
    this.ttlSecondsAfterFinished = ttlSecondsAfterFinished;
    this.podFailurePolicy = podFailurePolicy;
    this.template = template;
  }

  public String getName() {
    return name;
  }

  public String getMainContainer() {
    return mainContainer;
  }

  public int getBackoffLimit() {
    return backoffLimit;
  }

  public int getTtlSecondsAfterFinished() {
    return ttlSecondsAfterFinished;
  }

  public PodFailurePolicy getPodFailurePolicy() {
    return podFailurePolicy;
  }

  public PodTemplateSpec getTemplate() {
    return template;
  }
}
