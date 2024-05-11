package dev.sushigumi.milkyway.core.database;

import dev.sushigumi.milkyway.core.database.entities.TestPlan;
import dev.sushigumi.milkyway.core.database.projections.TestPlanSummary;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TestPlanRepository implements PanacheMongoRepository<TestPlan> {
  public List<TestPlanSummary> getAllTestPlanSummaries() {
    return findAll().project(TestPlanSummary.class).list();
  }

  public List<TestPlanSummary> getAllTestPlanSummariesByName(String name) {
    return find("name", name).project(TestPlanSummary.class).list();
  }
}
