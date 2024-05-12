package dev.sushigumi.milkyway.endpoints.v1;

import dev.sushigumi.milkyway.kubernetes.TestTemplateService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/api/v1/templates")
public class TestTemplateResource {
  private final TestTemplateService testTemplateService;

  public TestTemplateResource(TestTemplateService testTemplateService) {
    this.testTemplateService = testTemplateService;
  }

  @Path("/{name}")
  @GET
  public String getTestTemplate(@PathParam("name") String name) {
    String yaml = testTemplateService.getTestTemplateAsYaml(name);
    if (yaml == null) {
      throw new NotFoundException();
    }

    return yaml;
  }
}
