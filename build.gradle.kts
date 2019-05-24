plugins {
    base
    kotlin("jvm") version "1.3.31" apply false
}

allprojects {
    group = "de.bebauer"

    repositories {
        jcenter()
    }
}

subprojects {
    val kotlinVersion by extra("1.3.31")
    val springVersion by extra("5.1.7.RELEASE")
    val springBootVersion by extra("2.1.5.RELEASE")
    val jacksonVersion by extra("2.9.9")
    val jUnitPlatformConsoleVersion by extra("1.4.2")
    val arrowVersion by extra("0.8.2")
    val kotlinTestVersion by extra("3.3.2")

    tasks.withType<Jar> {
        archiveBaseName.set("webflux-handler-${project.name}")
    }
}