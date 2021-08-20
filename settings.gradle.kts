enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("kotest", "4.6.1")
            version("spring", "5.3.6")
            version("arrow", "0.13.2")
            version("mockk", "1.10.0")

            alias("arrow-core").to("io.arrow-kt", "arrow-core").versionRef("arrow")
            alias("arrow-fx-coroutines").to("io.arrow-kt", "arrow-fx-coroutines").versionRef("arrow")

            alias("kotest-assertions-core").to("io.kotest", "kotest-assertions-core").versionRef("kotest")
            alias("kotest-runner-junit5").to("io.kotest", "kotest-runner-junit5").versionRef("kotest")

            alias("spring-webflux").to("org.springframework", "spring-webflux").versionRef("spring")
            alias("spring-test").to("org.springframework", "spring-test").versionRef("spring")
            alias("spring-context").to("org.springframework", "spring-context").versionRef("spring")

            alias("reactor-kotlin-extensions").to("io.projectreactor.kotlin", "reactor-kotlin-extensions")
                .version("1.1.3")

            alias("jackson-module-kotlin").to("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")

            alias("mockk-core").to("io.mockk", "mockk").versionRef("mockk")
            alias("mockk-common").to("io.mockk", "mockk-common").versionRef("mockk")
        }
    }
}

rootProject.name = "webflux-handler-dsl"

include("dsl", "example")
