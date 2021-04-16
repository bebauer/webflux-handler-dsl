import de.bebauer.webflux.handler.dsl.codegen.CodeGen
import de.bebauer.webflux.handler.dsl.codegen.codeGenOutputDir
import de.bebauer.webflux.handler.dsl.codegen.codeGenTestOutputDir
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    id("com.jfrog.bintray")
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