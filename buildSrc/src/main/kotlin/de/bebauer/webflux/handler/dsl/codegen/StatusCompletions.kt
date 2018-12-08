package de.bebauer.webflux.handler.dsl.codegen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File

internal fun generateStatusCompletions(outputDir: File, testOutDir: File) {
    val srcFileBuilder = FileSpec.builder("de.bebauer.webflux.handler.dsl", "StatusCompletionsGenerated")
        .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("\"UnassignedFluxMonoInstance\"").build())

    val testClass = TypeSpec.classBuilder("StatusCompletionsGeneratedTests")

    val statusList =
        listOf("OK", "NOT_FOUND", "BAD_REQUEST", "FORBIDDEN", "INTERNAL_SERVER_ERROR", "UNAUTHORIZED", "CREATED")

    val handlerDsl = ClassName("de.bebauer.webflux.handler.dsl", "HandlerDsl")
    val mono = ClassName("reactor.core.publisher", "Mono")
    val flux = ClassName("reactor.core.publisher", "Flux")
    val serverResponse = ClassName("org.springframework.web.reactive.function.server", "ServerResponse")
    val httpStatus = ClassName("org.springframework.http", "HttpStatus")
    val completeOperation = ClassName("de.bebauer.webflux.handler.dsl", "CompleteOperation")
    val bodyInserter = ClassName("org.springframework.web.reactive.function", "BodyInserter")
    val serverHttpResponse = ClassName("org.springframework.http.server.reactive", "ServerHttpResponse")
    val test = ClassName("org.junit.jupiter.api", "Test")

    fun generateWithBuilder(status: String) {
        srcFileBuilder.addFunction(
            FunSpec.builder(status.underscoreToCamelCase())
                .receiver(handlerDsl)
                .addParameter(
                    ParameterSpec.builder(
                        "init",
                        LambdaTypeName.get(
                            receiver = serverResponse.nestedClass("BodyBuilder"),
                            returnType = mono.parameterizedBy(serverResponse)
                        )
                    ).defaultValue(CodeBlock.of("{ build() }")).build()
                )
                .returns(completeOperation)
                .addStatement("return complete(%T.$status, init)", httpStatus)
                .build()
        )

        testClass.addFunction(
            FunSpec.builder("${status.underscoreToCamelCase()} should complete without body")
                .addAnnotation(test)
                .addStatement(
                    """
                        runHandlerTest(
                            handler {
                                ${status.underscoreToCamelCase()}()
                            },
                            {
                                expectStatus().${status.statusToCheck()}
                                    .expectBody(String::class.java).returnResult()
                                    .apply { assertThat(responseBody).isNullOrEmpty() }
                            })
                    """.trimIndent()
                )
                .build()
        )

        testClass.addFunction(
            FunSpec.builder("${status.underscoreToCamelCase()} should complete with body builder")
                .addAnnotation(test)
                .addStatement(
                    """
                        runHandlerTest(
                            handler {
                                ${status.underscoreToCamelCase()} {
                                    body(fromObject("test"))
                                }
                            },
                            {
                                expectStatus().${status.statusToCheck()}
                                    .expectBody(String::class.java).returnResult()
                                    .apply { assertThat(responseBody).isEqualTo("test") }
                            })
                    """.trimIndent()
                )
                .build()
        )
    }

    fun generateWithFlux(status: String) {
        val typeT = TypeVariableName("T")

        srcFileBuilder.addFunction(
            FunSpec.builder(status.underscoreToCamelCase())
                .addModifiers(KModifier.INLINE)
                .addTypeVariable(typeT.copy(reified = true))
                .receiver(handlerDsl)
                .addParameter(
                    "flux",
                    flux.parameterizedBy(typeT)
                )
                .returns(completeOperation)
                .addStatement("return complete(%T.$status, flux)", httpStatus)
                .build()
        )

        testClass.addFunction(
            FunSpec.builder("${status.underscoreToCamelCase()} should complete with flux")
                .addAnnotation(test)
                .addStatement(
                    """
                        runHandlerTest(
                            handler {
                                ${status.underscoreToCamelCase()}(Flux.fromIterable(listOf(1, 2, 3)))
                            },
                            {
                                expectStatus().${status.statusToCheck()}
                                    .expectBodyList(Int::class.java).returnResult()
                                    .apply { assertThat(responseBody).containsExactly(1, 2, 3) }
                            })
                    """.trimIndent()
                )
                .build()
        )
    }

    fun generateWithMono(status: String) {
        val typeT = TypeVariableName("T")

        srcFileBuilder.addFunction(
            FunSpec.builder(status.underscoreToCamelCase())
                .addModifiers(KModifier.INLINE)
                .addTypeVariable(typeT.copy(reified = true))
                .receiver(handlerDsl)
                .addParameter(
                    "mono",
                    mono.parameterizedBy(typeT)
                )
                .returns(completeOperation)
                .addStatement("return complete(%T.$status, mono)", httpStatus)
                .build()
        )

        testClass.addFunction(
            FunSpec.builder("${status.underscoreToCamelCase()} should complete with mono")
                .addAnnotation(test)
                .addStatement(
                    """
                        runHandlerTest(
                            handler {
                                ${status.underscoreToCamelCase()}(Mono.just(123))
                            },
                            {
                                expectStatus().${status.statusToCheck()}
                                    .expectBody(Int::class.java).returnResult()
                                    .apply { assertThat(responseBody).isEqualTo(123) }
                            })
                    """.trimIndent()
                )
                .build()
        )
    }

    fun generateWithValue(status: String) {
        val typeT = TypeVariableName("T")

        srcFileBuilder.addFunction(
            FunSpec.builder(status.underscoreToCamelCase())
                .addModifiers(KModifier.INLINE)
                .addTypeVariable(typeT.copy(reified = true))
                .receiver(handlerDsl)
                .addParameter(
                    "value",
                    typeT.copy(nullable = true)
                )
                .returns(completeOperation)
                .addStatement("return complete(%T.$status, value)", httpStatus)
                .build()
        )

        testClass.addFunction(
            FunSpec.builder("${status.underscoreToCamelCase()} should complete with value")
                .addAnnotation(test)
                .addStatement(
                    """
                        runHandlerTest(
                            handler {
                                ${status.underscoreToCamelCase()}("123")
                            },
                            {
                                expectStatus().${status.statusToCheck()}
                                    .expectBody(String::class.java).returnResult()
                                    .apply { assertThat(responseBody).isEqualTo("123") }
                            })
                    """.trimIndent()
                )
                .build()
        )
    }

    fun generateWithBodyInserter(status: String) {
        srcFileBuilder.addFunction(
            FunSpec.builder(status.underscoreToCamelCase())
                .receiver(handlerDsl)
                .addParameter(
                    "inserter",
                    bodyInserter.parameterizedBy(
                        STAR,
                        WildcardTypeName.consumerOf(serverHttpResponse)
                    )
                )
                .returns(completeOperation)
                .addStatement("return complete(%T.$status, inserter)", httpStatus)
                .build()
        )

        testClass.addFunction(
            FunSpec.builder("${status.underscoreToCamelCase()} should complete with body inserter")
                .addAnnotation(test)
                .addStatement(
                    """
                        runHandlerTest(
                            handler {
                                ${status.underscoreToCamelCase()}(fromObject("123"))
                            },
                            {
                                expectStatus().${status.statusToCheck()}
                                    .expectBody(String::class.java).returnResult()
                                    .apply { assertThat(responseBody).isEqualTo("123") }
                            })
                    """.trimIndent()
                )
                .build()
        )
    }

    statusList.map { status ->
        generateWithBuilder(status)
        generateWithFlux(status)
        generateWithMono(status)
        generateWithValue(status)
        generateWithBodyInserter(status)
    }

    srcFileBuilder.build().writeTo(outputDir)

    FileSpec.builder("de.bebauer.webflux.handler.dsl", "StatusCompletionsGeneratedTests")
        .addImport("org.assertj.core.api", "Assertions.assertThat")
        .addImport("org.springframework.web.reactive.function", "BodyInserters.fromObject")
        .addImport("reactor.core.publisher", "Flux")
        .addImport("reactor.core.publisher", "Mono")
        .addType(testClass.build())
        .build()
        .writeTo(testOutDir)
}

private fun String.underscoreToCamelCase() = "_([a-z\\d])".toRegex().replace(this.toLowerCase()) { m ->
    m.groups[1]?.value?.toUpperCase()!!
}

private fun String.firstToUpper() = this[0].toUpperCase() + this.substring(1)

private fun String.statusToCheck() = "is${when (this) {
    "INTERNAL_SERVER_ERROR" -> "5xx_SERVER_ERROR"
    else -> this
}.underscoreToCamelCase().firstToUpper()}"
