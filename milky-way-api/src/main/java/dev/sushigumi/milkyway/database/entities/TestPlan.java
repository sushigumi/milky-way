package dev.sushigumi.milkyway.database.entities;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;

@MongoEntity(collection = TestPlan.COLLECTION_NAME)
public class TestPlan {
  public static final String COLLECTION_NAME = "test-plans";

  public ObjectId id;
  public String name;
  public List<Test> tests;
  public Map<String, String> baselineEnvVars;
  public Map<String, String> candidateEnvVars;
}
