package dev.sushigumi.milkyway.core.database;

import dev.sushigumi.milkyway.core.database.entities.KubernetesTestSpec;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KubernetesTestConfigRepository
    implements PanacheMongoRepositoryBase<KubernetesTestSpec, String> {}
