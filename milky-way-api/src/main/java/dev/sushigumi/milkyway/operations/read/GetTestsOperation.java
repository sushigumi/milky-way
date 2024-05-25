package dev.sushigumi.milkyway.operations.read;

import dev.sushigumi.milkyway.database.TestRepository;
import dev.sushigumi.milkyway.database.entities.Test;
import dev.sushigumi.milkyway.database.entities.TestPlan;
import dev.sushigumi.milkyway.operations.Operation;
import dev.sushigumi.milkyway.operations.OperationContext;
import java.util.List;
import org.bson.Document;

public class GetTestsOperation extends Operation<List<Test>> {
  private final Query query;

  public GetTestsOperation(Query query) {
    this.query = query;
  }

  @Override
  public void execute(OperationContext context) {
    final TestRepository testRepository = context.getTestRepository();
    result = testRepository.find(query.getDocument()).list();
  }

  public interface Query {
    Document getDocument();
  }

  public static class WithTestPlanQuery implements Query {
    private final TestPlan testPlan;

    public WithTestPlanQuery(TestPlan testPlan) {
      this.testPlan = testPlan;
    }

    @Override
    public Document getDocument() {
      return new Document("_id", new Document("$in", testPlan.testIds));
    }
  }
}
