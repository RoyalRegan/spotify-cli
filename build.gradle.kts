plugins {
    kotlin("jvm") version "1.6.10"
    application
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

group = "com.jaju"
version = "0.0.1"

val mainClassPath = "com.jaju.Main"

//versions
val corutines = "1.6.1"
val spotifyApiKotlin = "3.8.6"
val clikt = "3.4.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$corutines")

    implementation("com.github.ajalt.clikt:clikt:$clikt")
    implementation("com.adamratzman:spotify-api-kotlin-core:$spotifyApiKotlin")
}

application {
    mainClass.set(mainClassPath)
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = mainClassPath
    }
}
