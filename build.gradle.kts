plugins {
    base
    kotlin("jvm") version "1.3.21" apply false
}

allprojects {
    group = "de.bebauer"

    repositories {
        jcenter()
    }
}

subprojects {
    val kotlinVersion by extra("1.3.21")
    val springVersion by extra("5.1.5.RELEASE")
    val springBootVersion by extra("2.1.3.RELEASE")
    val jacksonVersion by extra("2.9.8")
    val jUnitPlatformConsoleVersion by extra("1.2.0")
    val arrowVersion by extra("0.8.2")
    val kotlinTestVersion by extra("3.1.10")

    tasks.withType<Jar> {
        baseName = "webflux-handler-${project.name}"
    }
}