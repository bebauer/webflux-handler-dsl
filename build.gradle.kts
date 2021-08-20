plugins {
    base
    kotlin("jvm") version "1.5.21" apply false
    kotlin("plugin.spring") version "1.5.21" apply false
    id("org.springframework.boot") version "2.4.5" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
    id("pl.allegro.tech.build.axion-release") version "1.13.2"
}

val jvmTargetVersion: JavaVersion by extra(JavaVersion.VERSION_14)

scmVersion {
    tag(closureOf<pl.allegro.tech.build.axion.release.domain.TagNameSerializationConfig> {
        prefix = "release-"
    })

    versionIncrementer = pl.allegro.tech.build.axion.release.domain.PredefinedVersionIncrementer.versionIncrementerFor("incrementMinor")

    hooks(closureOf<pl.allegro.tech.build.axion.release.domain.hooks.HooksConfig> {
        pre(
            "fileUpdate",
            mapOf(
                "files" to listOf(
                    "README.md",
                    "docs/gettingStarted/gradle.md",
                    "docs/gettingStarted/maven.md"
                ),
                "pattern" to KotlinClosure2<String, pl.allegro.tech.build.axion.release.domain.hooks.HookContext, String>(
                    { v, _ -> v.replace(".", "\\.") }),
                "replacement" to KotlinClosure2<String, pl.allegro.tech.build.axion.release.domain.hooks.HookContext, String>(
                    { v, _ -> v })
            )
        )
        pre("commit")
    })
}

allprojects {
    version = rootProject.scmVersion.version
    group = "de.bebauer"

    repositories {
        mavenCentral()
    }
}

subprojects {
    tasks.withType<Jar> {
        archiveBaseName.set("webflux-handler-${project.name}")
    }
}