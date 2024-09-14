plugins {
    kotlin("jvm") version "2.0.0"
}

group = "net.insi8.scoreboard"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}