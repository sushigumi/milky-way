package dev.sushigumi.milkyway.endpoints.v1;

import dev.sushigumi.milkyway.KubernetesService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/api/v1/groups")
public class TestGroupResource {
  private final KubernetesService kubernetesService;

  public TestGroupResource(KubernetesService kubernetesService) {
    this.kubernetesService = kubernetesService;
  }

  @Path("/{groupName}")
  @GET
  public String getTestGroup(@PathParam("groupName") String groupName) {
    String yaml = kubernetesService.getTestGroupResource(groupName);
    if (yaml == null) {
      throw new NotFoundException();
    }

    return yaml;
  }
}
