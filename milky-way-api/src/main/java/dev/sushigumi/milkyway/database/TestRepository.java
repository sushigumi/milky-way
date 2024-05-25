package dev.sushigumi.milkyway.database;

import dev.sushigumi.milkyway.database.entities.Test;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TestRepository implements PanacheMongoRepository<Test> {}
