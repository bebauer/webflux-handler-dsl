import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import pl.allegro.tech.build.axion.release.domain.PredefinedVersionIncrementer
import webflux.handler.dsl.codegen.CodeGen
import webflux.handler.dsl.codegen.codeGenOutputDir

plugins {
    kotlin("jvm") version "1.3.10"
    id("com.jfrog.bintray") version "1.8.4"
    `maven-publish`
    `project-report`
    id("pl.allegro.tech.build.axion-release") version "1.10.0"
}

group = "webflux-handler-dsl"
version = scmVersion.version

repositories {
    jcenter()
}

scmVersion {
    versionIncrementer = PredefinedVersionIncrementer.versionIncrementerFor("incrementMinor")
}

val springVersion = "5.1.2.RELEASE"
val junitVersion = "5.3.2"
val assertJVersion = "3.11.1"
val jacksonVersion = "2.9.7"
val jUnitPlatformConsoleVersion = "1.2.0"
val arrowVersion = "0.8.1"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.springframework:spring-webflux:$springVersion")
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testRuntime("org.junit.platform:junit-platform-console:$jUnitPlatformConsoleVersion")
    testImplementation("org.springframework:spring-test:$springVersion")
    testImplementation("org.springframework:spring-context:$springVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    // undo 1.3 workaround with next jackson kotlin version
    testImplementation(kotlin("reflect"))
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion") {
        exclude(module = "kotlin-reflect")
    }
}

sourceSets["main"].java {
    srcDir(codeGenOutputDir())
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val codeGen by tasks.registering(CodeGen::class)

tasks.withType<KotlinCompile> {
    dependsOn(codeGen)

    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
}

val sourcesJar by tasks.registering(Jar::class) {
    dependsOn(JavaPlugin.CLASSES_TASK_NAME, codeGen)
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

val publicationName = "maven"

publishing {
    publications {
        register(publicationName, MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}

bintray {
    user = (project.properties["bintray.user"] ?: System.getenv("BINTRAY_USER"))?.toString()
    key = (project.properties["bintray.key"] ?: System.getenv("BINTRAY_KEY"))?.toString()
    setPublications(publicationName)
    with(pkg) {
        repo = "maven"
        name = "webflux-handler-dsl"
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/bebauer/webflux-handler-dsl"
        with(version) {
            name = project.version.toString()
            desc = "${project.description} ${project.version}"
            vcsTag = project.version.toString()
        }
    }
    publish = (project.properties["bintray.publish"] ?: "true").toString().toBoolean()
    override = (project.properties["bintray.override"] ?: "false").toString().toBoolean()
    dryRun = (project.properties["bintray.dryrun"] ?: "false").toString().toBoolean()
}