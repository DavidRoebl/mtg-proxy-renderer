plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "dev.roebl"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("io.ktor:ktor-client-core:2.3.9")
    implementation("io.ktor:ktor-client-cio:2.3.9")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.apache.pdfbox:pdfbox:2.0.26")
    implementation("org.apache.commons:commons-collections4:4.4")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}