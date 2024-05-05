group = "dev.sushigumi"
version = "1.0-SNAPSHOT"

plugins {
    id("io.quarkus") version Versions.QUARKUS apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}
