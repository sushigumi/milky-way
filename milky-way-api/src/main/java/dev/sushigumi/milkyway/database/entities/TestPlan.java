package dev.sushigumi.milkyway.database.entities;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;

@MongoEntity(database = "milky-way", collection = "test-collections")
public class TestPlan {
  public ObjectId id;
  public String name;
  public List<Test> tests;
  public Map<String, String> baselineConfiguration;
  public Map<String, String> candidateConfiguration;
}
