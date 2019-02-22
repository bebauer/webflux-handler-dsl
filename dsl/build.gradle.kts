import de.bebauer.webflux.handler.dsl.codegen.CodeGen
import de.bebauer.webflux.handler.dsl.codegen.codeGenOutputDir
import de.bebauer.webflux.handler.dsl.codegen.codeGenTestOutputDir
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import pl.allegro.tech.build.axion.release.domain.PredefinedVersionIncrementer
import pl.allegro.tech.build.axion.release.domain.hooks.HookContext
import pl.allegro.tech.build.axion.release.domain.hooks.HooksConfig

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.jfrog.bintray") version "1.8.4"
    `maven-publish`
    `project-report`
    id("pl.allegro.tech.build.axion-release") version "1.10.0"
}

version = scmVersion.version

scmVersion {
    versionIncrementer = PredefinedVersionIncrementer.versionIncrementerFor("incrementMinor")

    hooks(closureOf<HooksConfig> {
        pre(
            "fileUpdate",
            mapOf(
                "files" to listOf("README.md", "docs/gettingStarted/gradle.md", "docs/gettingStarted/maven.md"),
                "pattern" to KotlinClosure2<String, HookContext, String>({ v, _ -> v.replace(".", "\\.") }),
                "replacement" to KotlinClosure2<String, HookContext, String>({ v, _ -> v })
            )
        )
        pre("commit")
    })
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.springframework:spring-webflux:${extra["springVersion"]}")
    implementation("io.arrow-kt:arrow-core:${extra["arrowVersion"]}")
    testImplementation("org.springframework:spring-test:${extra["springVersion"]}")
    testImplementation("org.springframework:spring-context:${extra["springVersion"]}")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:${extra["jacksonVersion"]}") {
        exclude(module = "kotlin-reflect")
    }
    testImplementation("io.kotlintest:kotlintest-runner-junit5:${extra["kotlinTestVersion"]}")
}

sourceSets["main"].java {
    srcDir(codeGenOutputDir())
}

sourceSets["test"].java {
    srcDir(codeGenTestOutputDir())
}

tasks.withType<Test> {
    @Suppress("UnstableApiUsage")
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
            artifactId = "webflux-handler-dsl"
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