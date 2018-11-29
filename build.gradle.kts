import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import webflux.handler.dsl.codegen.CodeGen
import webflux.handler.dsl.codegen.codeGenOutputDir

plugins {
    kotlin("jvm") version "1.3.10"
}

group = "webflux-handler-dsl"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
}

val springVersion = "5.1.2.RELEASE"
val junitVersion = "5.3.2"
val assertJVersion = "3.11.1"
val jacksonVersion = "2.9.7"
val jUnitPlatformConsoleVersion = "1.2.0"
val arrowVersion = "0.8.1"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.springframework:spring-webflux:$springVersion")
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("io.arrow-kt:arrow-syntax:$arrowVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testRuntime("org.junit.platform:junit-platform-console:$jUnitPlatformConsoleVersion")
    testImplementation("org.springframework:spring-test:$springVersion")
    testImplementation("org.springframework:spring-context:$springVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
}

sourceSets["main"].java {
    srcDir(codeGenOutputDir())
}

tasks {
    "test"(Test::class) {
        useJUnitPlatform()
    }

    create("codegen", CodeGen::class)

    withType<KotlinCompile> {
        dependsOn("codegen")
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}
