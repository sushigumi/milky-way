plugins {
    id("quarkus-conventions")
    id("io.quarkus")
}

dependencies {
    implementation("io.quarkus:quarkus-mongodb-panache")
    implementation("io.quarkus:quarkus-kubernetes-client")
}