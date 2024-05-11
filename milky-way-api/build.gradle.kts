plugins {
    id("quarkus-conventions")
    id("io.quarkus")
}

dependencies {
    implementation(project(":milky-way-kubernetes"))

    implementation("io.quarkus:quarkus-mongodb-panache")
    implementation("io.quarkus:quarkus-kubernetes-client")
    implementation("io.quarkus:quarkus-hibernate-validator")

    testImplementation("io.quarkus:quarkus-test-kubernetes-client")
}