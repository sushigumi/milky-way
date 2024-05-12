package dev.sushigumi.milkyway.endpoints.v1;

import dev.sushigumi.milkyway.KubernetesService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/api/v1/templates")
public class TestTemplateResource {
  private final KubernetesService kubernetesService;

  public TestTemplateResource(KubernetesService kubernetesService) {
    this.kubernetesService = kubernetesService;
  }

  @Path("/{name}")
  @GET
  public String getTestTemplate(@PathParam("name") String name) {
    String yaml = kubernetesService.getTestTemplateAsYaml(name);
    if (yaml == null) {
      throw new NotFoundException();
    }

    return yaml;
  }
}
