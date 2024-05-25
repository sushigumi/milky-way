package dev.sushigumi.milkyway.endpoints.v1;

import dev.sushigumi.milkyway.operations.read.GetTestTemplateOperation;
import dev.sushigumi.milkyway.services.OperationExecutorService;
import io.fabric8.kubernetes.client.utils.Serialization;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/api/v1/templates")
public class TestTemplateResource {
  private final OperationExecutorService executorService;

  public TestTemplateResource(OperationExecutorService executorService) {
    this.executorService = executorService;
  }

  @Path("/{name}")
  @GET
  public String getTestTemplate(@PathParam("name") String name) {
    final var operation = new GetTestTemplateOperation(name);
    executorService.execute(operation);

    return Serialization.asYaml(operation.getResult());
  }
}
