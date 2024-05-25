package dev.sushigumi.milkyway.database.entities;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;

@MongoEntity(collection = Test.COLLECTION_NAME)
public class Test {
  public static final String COLLECTION_NAME = "tests";

  public ObjectId id;
  public String name;
  public TestStatus status;
}
