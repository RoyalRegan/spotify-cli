plugins {
    kotlin("jvm") version "1.6.10"
    application
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

group = "com.jaju"
version = "0.0.1"

val mainClassPath = "com.jaju.Main"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

application {
    mainClass.set(mainClassPath)
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = mainClassPath
    }
}
