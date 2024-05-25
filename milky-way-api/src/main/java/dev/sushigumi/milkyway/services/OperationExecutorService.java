package dev.sushigumi.milkyway.services;

import dev.sushigumi.milkyway.operations.Operation;
import dev.sushigumi.milkyway.operations.OperationContext;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OperationExecutorService {
  private final OperationContext context;

  public OperationExecutorService(OperationContext context) {
    this.context = context;
  }

  public <T> T execute(Operation<T> operation) {
    operation.execute(context);
    return operation.getResult();
  }
}
