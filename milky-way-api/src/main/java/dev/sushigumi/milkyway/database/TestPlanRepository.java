package dev.sushigumi.milkyway.database;

import dev.sushigumi.milkyway.database.entities.TestPlan;
import dev.sushigumi.milkyway.database.projections.TestPlanSummary;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TestPlanRepository implements PanacheMongoRepository<TestPlan> {
  public List<TestPlanSummary> getAllTestPlanSummaries() {
    return findAll().project(TestPlanSummary.class).list();
  }
}
