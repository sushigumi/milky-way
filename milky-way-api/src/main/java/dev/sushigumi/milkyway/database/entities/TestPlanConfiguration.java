package dev.sushigumi.milkyway.database.entities;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.util.List;
import org.bson.types.ObjectId;

@MongoEntity(collection = TestPlanConfiguration.COLLECTION_NAME)
public class TestPlanConfiguration {
  public static final String COLLECTION_NAME = "test-plan-configurations";

  public ObjectId id;
  public String name;
  public List<String> testTemplates;
}
