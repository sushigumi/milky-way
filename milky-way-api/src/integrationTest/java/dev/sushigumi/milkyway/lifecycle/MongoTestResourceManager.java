package dev.sushigumi.milkyway.lifecycle;

import com.google.common.collect.ImmutableMap;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.sushigumi.milkyway.database.entities.TestPlan;
import io.quarkus.test.common.DevServicesContext;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bson.Document;
import org.bson.types.ObjectId;

public class MongoTestResourceManager
    implements QuarkusTestResourceLifecycleManager, DevServicesContext.ContextAware {
  public static final String DATABASE_NAME = "milky-way-test";

  private String connectionString;

  @Override
  public void setIntegrationTestContext(DevServicesContext context) {
    Map<String, String> properties = context.devServicesProperties();
    connectionString = properties.get("quarkus.mongodb.connection-string");
  }

  private Document getDocument(String documentPath) throws IOException {
    try (InputStream stream = getClass().getClassLoader().getResourceAsStream(documentPath)) {
      String json =
          new String(Objects.requireNonNull(stream).readAllBytes(), StandardCharsets.UTF_8);
      return Document.parse(json);
    }
  }

  private void addTestPlan(MongoCollection<Document> collection, String documentPath)
      throws IOException {
    final Document document = getDocument(documentPath);

    // Convert each string representation of ObjectId into ObjectId
    List<String> testIdStrings = (List<String>) document.get("testIds");
    List<ObjectId> testIds = new ArrayList<>(testIdStrings.size());
    for (var testIdString : testIdStrings) {
      ObjectId testId = new ObjectId(testIdString);
      testIds.add(testId);
    }

    document.put("testIds", testIds);

    collection.insertOne(document);
  }

  @Override
  public Map<String, String> start() {
    try (MongoClient client = MongoClients.create(connectionString)) {
      MongoDatabase database = client.getDatabase(DATABASE_NAME);
      MongoCollection<Document> testPlanCollection =
          database.getCollection(TestPlan.COLLECTION_NAME);
      addTestPlan(testPlanCollection, "test-plans/1.json");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return ImmutableMap.of("quarkus.mongodb.database", DATABASE_NAME);
  }

  @Override
  public void stop() {}
}
