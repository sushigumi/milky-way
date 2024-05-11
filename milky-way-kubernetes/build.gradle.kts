plugins {
    id("java-conventions")
}

dependencies {
    annotationProcessor("io.fabric8:crd-generator-apt:${Versions.FABRIC8}")
    implementation("io.fabric8:kubernetes-client:${Versions.FABRIC8}")
    implementation("io.fabric8:generator-annotations:${Versions.FABRIC8}")

    testImplementation("io.fabric8:kubernetes-server-mock:${Versions.FABRIC8}")
}
