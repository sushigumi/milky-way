package dev.sushigumi.milkyway.core.database.projections;

import dev.sushigumi.milkyway.core.database.entities.TestPlan;
import io.quarkus.mongodb.panache.common.ProjectionFor;
import org.bson.types.ObjectId;

@ProjectionFor(TestPlan.class)
public class TestPlanSummary {
  public ObjectId id;
  public String name;
}
