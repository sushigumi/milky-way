package dev.sushigumi.milkyway.core.database.entities;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonId;

@MongoEntity(database = "milky-way", collection = "kubernetes-test-specs")
public class KubernetesTestSpec {
  @BsonId public String id;
  public String absolutePath;
  public String resourcePath;
}
