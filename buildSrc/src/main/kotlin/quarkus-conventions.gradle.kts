plugins {
    id("java-conventions")
}

dependencies {
    implementation(platform("io.quarkus:quarkus-bom:${Versions.QUARKUS}"))
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-config-yaml")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.quarkus:quarkus-junit5-mockito")
    testImplementation("io.rest-assured:rest-assured")
}

