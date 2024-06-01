package dev.sushigumi.milkyway.endpoints.v1;

import dev.sushigumi.milkyway.kubernetes.api.model.TestPlanTemplate;
import dev.sushigumi.milkyway.operations.read.GetTestPlanTemplateOperation;
import dev.sushigumi.milkyway.operations.read.GetTestPlanTemplatesOperation;
import dev.sushigumi.milkyway.operations.read.GetTestTemplateOperation;
import dev.sushigumi.milkyway.services.OperationExecutorService;
import io.fabric8.kubernetes.client.utils.Serialization;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import java.util.List;

@Path("/api/v1/templates")
public class CrdTemplateResource {
  private final OperationExecutorService executorService;

  public CrdTemplateResource(OperationExecutorService executorService) {
    this.executorService = executorService;
  }

  @Path("/plans")
  @GET
  public List<TestPlanTemplate> getTestPlanTemplates() {
    return executorService.execute(new GetTestPlanTemplatesOperation());
  }

  @Path("/plans/{name}")
  @GET
  public String getTestPlanTemplate(@PathParam("name") String name) {
    return Serialization.asYaml(executorService.execute(new GetTestPlanTemplateOperation(name)));
  }

  @Path("/tests/{name}")
  @GET
  public String getTestTemplate(@PathParam("name") String name) {
    return Serialization.asYaml(executorService.execute(new GetTestTemplateOperation(name)));
  }
}
