import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jvmTargetVersion: JavaVersion by rootProject.extra

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

java.sourceCompatibility = jvmTargetVersion

dependencies {
    implementation(project(":dsl"))

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation(libs.arrow.core)

    runtimeOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = jvmTargetVersion.toString()
    kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
}