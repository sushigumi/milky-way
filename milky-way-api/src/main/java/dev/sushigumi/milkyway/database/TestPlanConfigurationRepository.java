package dev.sushigumi.milkyway.database;

import dev.sushigumi.milkyway.database.entities.TestPlanConfiguration;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TestPlanConfigurationRepository
    implements PanacheMongoRepository<TestPlanConfiguration> {}
