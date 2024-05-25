package dev.sushigumi.milkyway.operations;

public abstract class Operation<T> implements Executable {
  protected T result;

  public T getResult() {
    return result;
  }
}
