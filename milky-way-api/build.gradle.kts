plugins {
    id("quarkus-conventions")
    id("io.quarkus")
}

dependencies {
    implementation(project(":milky-way-kubernetes"))

    implementation("io.quarkus:quarkus-mongodb-panache")
    implementation("io.quarkus:quarkus-kubernetes-client")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("org.jboss.slf4j:slf4j-jboss-logmanager")

    // Bouncy castle is required for Kubernetes clients that use Elliptic Curve keys.
    implementation("org.bouncycastle:bcpkix-jdk18on")

    testImplementation("io.quarkus:quarkus-test-kubernetes-client")
}