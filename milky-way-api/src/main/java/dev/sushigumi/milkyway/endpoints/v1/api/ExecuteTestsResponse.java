package dev.sushigumi.milkyway.endpoints.v1.api;

import java.util.List;

public class ExecuteTestsResponse {
  private final List<String> invalidTestIds;
  private final List<String> queuedTestIds;

  public ExecuteTestsResponse(List<String> invalidTestIds, List<String> queuedTestIds) {
    this.invalidTestIds = invalidTestIds;
    this.queuedTestIds = queuedTestIds;
  }

  public List<String> getInvalidTestIds() {
    return invalidTestIds;
  }

  public List<String> getQueuedTestIds() {
    return queuedTestIds;
  }
}
