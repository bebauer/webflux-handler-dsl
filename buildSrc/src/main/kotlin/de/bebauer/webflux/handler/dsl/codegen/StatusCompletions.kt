package de.bebauer.webflux.handler.dsl.codegen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.springframework.util.LinkedMultiValueMap
import java.io.File

internal fun generateStatusCompletions(outputDir: File, testOutDir: File) {
    val srcFileBuilder = FileSpec.builder("de.bebauer.webflux.handler.dsl", "StatusCompletionsGenerated")
        .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("\"UnassignedFluxMonoInstance\"").build())

    val statusList =
        listOf("OK", "NOT_FOUND", "BAD_REQUEST", "FORBIDDEN", "INTERNAL_SERVER_ERROR", "UNAUTHORIZED", "CREATED")

    val mono = ClassName("reactor.core.publisher", "Mono")
    val flux = ClassName("reactor.core.publisher", "Flux")
    val serverResponse = ClassName("org.springframework.web.reactive.function.server", "ServerResponse")
    val bodyBuilder = serverResponse.nestedClass("BodyBuilder")
    val httpStatus = ClassName("org.springframework.http", "HttpStatus")
    val bodyInserter = ClassName("org.springframework.web.reactive.function", "BodyInserter")
    val serverHttpResponse = ClassName("org.springframework.http.server.reactive", "ServerHttpResponse")
    val wordSpec = ClassName("io.kotlintest.specs", "WordSpec")
    val responseBuilderCompleteOperation =
        ClassName("de.bebauer.webflux.handler.dsl", "ResponseBuilderCompleteOperation")
    val monoBodyCompleteOperation = ClassName("de.bebauer.webflux.handler.dsl", "MonoBodyCompleteOperation")
    val valueCompleteOperation = ClassName("de.bebauer.webflux.handler.dsl", "ValueCompleteOperation")

    val tests = LinkedMultiValueMap<String, String>()

    fun generateWithBuilder(status: String) {
        srcFileBuilder.addFunction(
            FunSpec.builder(status.underscoreToCamelCase())
                .addParameter(
                    ParameterSpec.builder(
                        "init",
                        LambdaTypeName.get(
                            receiver = bodyBuilder,
                            returnType = mono.parameterizedBy(serverResponse)
                        )
                    ).defaultValue(CodeBlock.of("{ build() }")).build()
                )
                .returns(responseBuilderCompleteOperation)
                .addStatement("return complete(%T.$status, init)", httpStatus)
                .build()
        )

        tests.add(
            status, """
                "complete without body" {
                    runHandlerTest(
                        handler {
                            ${status.underscoreToCamelCase()}()
                        },
                        {
                            expectStatus().${status.statusToCheck()}
                                .expectBody(String::class.java)
                                .returnResult().responseBody.isNullOrEmpty() shouldBe true
                        })
                }
            """.trimIndent()
        )

        tests.add(
            status, """
                "complete with body builder" {
                    runHandlerTest(
                        handler {
                            ${status.underscoreToCamelCase()} {
                                body(fromObject("test"))
                            }
                        },
                        {
                            expectStatus().${status.statusToCheck()}
                                .expectBody(String::class.java)
                                .returnResult().responseBody shouldBe "test"
                        })
                }
            """.trimIndent()
        )
    }

    fun generateWithFlux(status: String) {
        val typeT = TypeVariableName("T")

        srcFileBuilder.addFunction(
            FunSpec.builder(status.underscoreToCamelCase())
                .addModifiers(KModifier.INLINE)
                .addTypeVariable(typeT.copy(reified = true))
                .addParameter(
                    "flux",
                    flux.parameterizedBy(typeT)
                )
                .addParameter(
                    ParameterSpec.builder(
                        "builderInit",
                        LambdaTypeName.get(receiver = bodyBuilder, returnType = bodyBuilder),
                        KModifier.NOINLINE
                    ).defaultValue("{ this }").build()
                )
                .returns(responseBuilderCompleteOperation)
                .addStatement("return complete(%T.$status, flux, builderInit)", httpStatus)
                .build()
        )

        tests.add(
            status, """
                "complete with 'Flux'" {
                    runHandlerTest(
                        handler {
                            ${status.underscoreToCamelCase()}(Flux.fromIterable(listOf(1, 2, 3))) {
                                header("test", "xyz")
                            }
                        },
                        {
                            expectStatus().${status.statusToCheck()}
                                .expectHeader().value("test") { it shouldBe "xyz" }
                                .expectBodyList(Int::class.java)
                                .returnResult().responseBody should containExactly(1, 2, 3)
                        })
                }
            """.trimIndent()
        )
    }

    fun generateWithMono(status: String) {
        val typeT = TypeVariableName("T")

        srcFileBuilder.addFunction(
            FunSpec.builder(status.underscoreToCamelCase())
                .addModifiers(KModifier.INLINE)
                .addTypeVariable(typeT.copy(reified = true))
                .addParameter(
                    "mono",
                    mono.parameterizedBy(typeT)
                )
                .addParameter(
                    ParameterSpec.builder(
                        "builderInit",
                        LambdaTypeName.get(receiver = bodyBuilder, returnType = bodyBuilder),
                        KModifier.NOINLINE
                    ).defaultValue("{ this }").build()
                )
                .returns(monoBodyCompleteOperation.parameterizedBy(typeT))
                .addStatement("return complete(%T.$status, mono, builderInit)", httpStatus)
                .build()
        )

        tests.add(
            status, """
                "complete with 'Mono'" {
                    runHandlerTest(
                        handler {
                            ${status.underscoreToCamelCase()}(Mono.just(123)) {
                                header("test", "xyz")
                            }
                        },
                        {
                            expectStatus().${status.statusToCheck()}
                                .expectHeader().value("test") { it shouldBe "xyz" }
                                .expectBody(Int::class.java)
                                .returnResult().responseBody shouldBe 123
                        })
                }
            """.trimIndent()
        )
    }

    fun generateWithValue(status: String) {
        val typeT = TypeVariableName("T")

        srcFileBuilder.addFunction(
            FunSpec.builder(status.underscoreToCamelCase())
                .addTypeVariable(typeT)
                .addParameter(
                    "value",
                    typeT.copy(nullable = true)
                )
                .addParameter(
                    ParameterSpec.builder(
                        "builderInit",
                        LambdaTypeName.get(receiver = bodyBuilder, returnType = bodyBuilder)
                    ).defaultValue("{ this }").build()
                )
                .returns(valueCompleteOperation.parameterizedBy(typeT))
                .addStatement("return complete(%T.$status, value, builderInit)", httpStatus)
                .build()
        )

        tests.add(
            status, """
                "complete with value" {
                    runHandlerTest(
                        handler {
                            ${status.underscoreToCamelCase()}("123") {
                                header("test", "xyz")
                            }
                        },
                        {
                            expectStatus().${status.statusToCheck()}
                                .expectHeader().value("test") { it shouldBe "xyz" }
                                .expectBody(String::class.java)
                                .returnResult().responseBody shouldBe "123"
                        })
                }
            """.trimIndent()
        )
    }

    fun generateWithBodyInserter(status: String) {
        srcFileBuilder.addFunction(
            FunSpec.builder(status.underscoreToCamelCase())
                .addParameter(
                    "inserter",
                    bodyInserter.parameterizedBy(
                        STAR,
                        WildcardTypeName.consumerOf(serverHttpResponse)
                    )
                )
                .addParameter(
                    ParameterSpec.builder(
                        "builderInit",
                        LambdaTypeName.get(receiver = bodyBuilder, returnType = bodyBuilder)
                    ).defaultValue("{ this }").build()
                )
                .returns(responseBuilderCompleteOperation)
                .addStatement("return complete(%T.$status, inserter, builderInit)", httpStatus)
                .build()
        )

        tests.add(
            status, """
                "complete with body inserter" {
                    runHandlerTest(
                        handler {
                            ${status.underscoreToCamelCase()}(fromObject("123")) {
                                header("test", "xyz")
                            }
                        },
                        {
                            expectStatus().${status.statusToCheck()}
                                .expectHeader().value("test") { it shouldBe "xyz" }
                                .expectBody(String::class.java)
                                .returnResult().responseBody shouldBe "123"
                        })
                }
            """.trimIndent()
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
        .addImport("io.kotlintest", "should", "shouldBe")
        .addImport("io.kotlintest.matchers.collections", "containExactly")
        .addImport("org.springframework.web.reactive.function", "BodyInserters.fromObject")
        .addImport("reactor.core.publisher", "Flux")
        .addImport("reactor.core.publisher", "Mono")
        .addType(
            TypeSpec.classBuilder("StatusCompletionsGeneratedTests")
                .superclass(wordSpec)
                .addInitializerBlock(
                    CodeBlock.of(
                        tests.keys.joinToString("\n\n") {
                            """
                            |"${it.toString().underscoreToCamelCase()}" should {
                            |   ${tests[it]?.joinToString("\n\n")}
                            |}
                            |""".trimMargin()
                        }
                    )
                ).build()
        )
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
