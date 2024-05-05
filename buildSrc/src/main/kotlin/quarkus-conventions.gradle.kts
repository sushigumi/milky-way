plugins {
    id("java-conventions")
    id("com.diffplug.spotless")
}

dependencies {
    implementation(platform("io.quarkus:quarkus-bom:${Versions.QUARKUS}"))
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-config-yaml")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
}


spotless {
    java {
        googleJavaFormat()
        removeUnusedImports()
        formatAnnotations()
        trimTrailingWhitespace()
        endWithNewline()
    }
}
