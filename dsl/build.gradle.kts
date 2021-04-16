import de.bebauer.webflux.handler.dsl.codegen.CodeGen
import de.bebauer.webflux.handler.dsl.codegen.codeGenOutputDir
import de.bebauer.webflux.handler.dsl.codegen.codeGenTestOutputDir
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val bebauerJfrogUser: String? by project
val bebauerJfrogPassword: String? by project

val jvmTargetVersion: JavaVersion by rootProject.extra
val kotlinVersion: String by rootProject.extra
val springVersion: String by rootProject.extra
val reactorKotlinVersion: String by rootProject.extra
val jacksonVersion: String by rootProject.extra
val arrowVersion: String by rootProject.extra
val kotlinTestVersion: String by rootProject.extra

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.jfrog.artifactory")
    `maven-publish`
    `project-report`
}

java.sourceCompatibility = jvmTargetVersion

dependencies {
    implementation(platform("io.arrow-kt:arrow-stack:$arrowVersion"))

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.springframework:spring-webflux:$springVersion")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:$reactorKotlinVersion")
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrowVersion")

    testImplementation("org.springframework:spring-test:$springVersion")
    testImplementation("org.springframework:spring-context:$springVersion")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion") {
        exclude(module = "kotlin-reflect")
    }
    testImplementation("io.kotest:kotest-runner-junit5:$kotlinTestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotlinTestVersion")
}

sourceSets["main"].java {
    srcDir(codeGenOutputDir())
}

sourceSets["test"].java {
    srcDir(codeGenTestOutputDir())
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val codeGen by tasks.registering(CodeGen::class)

tasks.withType<KotlinCompile> {
    dependsOn(codeGen)

    kotlinOptions.jvmTarget = jvmTargetVersion.toString()
    kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
}

val sourcesJar by tasks.registering(Jar::class) {
    dependsOn(JavaPlugin.CLASSES_TASK_NAME, codeGen)
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "webflux-handler-dsl"

            from(components["java"])
        }
    }
}

artifactory {
    setContextUrl("https://bebauer.jfrog.io/artifactory")

    publish(delegateClosureOf<org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig> {
        Repository().run {
            if (version.toString().endsWith("-SNAPSHOT")) {
                setRepoKey("maven-snapshots")
            } else {
                setRepoKey("maven-releases")
            }
            setUsername(bebauerJfrogUser)
            setPassword(bebauerJfrogPassword)
            setMavenCompatible(true)
        }

        defaults(delegateClosureOf<groovy.lang.GroovyObject> {
            invokeMethod("publications", "maven")
            setProperty("publishIvy", false)
        })
    })
    // Redefine basic properties of the build info object
    clientConfig.apply {
        isIncludeEnvVars = false
    }
}
