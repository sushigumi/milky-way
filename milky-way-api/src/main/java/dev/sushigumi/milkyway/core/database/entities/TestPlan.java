package dev.sushigumi.milkyway.core.database.entities;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;

@MongoEntity(database = "milky-way", collection = "test-collections")
public class TestPlan {
  public ObjectId id;
  public String name;
  public List<Test> tests;
  public Map<String, String> baselineProperties;
  public Map<String, String> candidateProperties;
}
