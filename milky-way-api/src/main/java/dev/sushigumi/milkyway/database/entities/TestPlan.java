package dev.sushigumi.milkyway.database.entities;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.util.List;
import org.bson.types.ObjectId;

@MongoEntity(collection = TestPlan.COLLECTION_NAME)
public class TestPlan {
  public static final String COLLECTION_NAME = "test-plans";

  public ObjectId id;
  public String name;
  public List<ObjectId> testIds;
}
