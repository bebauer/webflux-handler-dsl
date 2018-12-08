package de.bebauer.webflux.handler.dsl.codegen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

import java.io.File

internal fun generateParameterDsl(outputDir: File, testOutDir: File) {
    val fileBuilder = FileSpec.builder("de.bebauer.webflux.handler.dsl", "QueryParametersGenerated")

    val queryParam = ClassName("de.bebauer.webflux.handler.dsl", "QueryParameter")
    val handlerDsl = ClassName("de.bebauer.webflux.handler.dsl", "HandlerDsl")
    val test = ClassName("org.junit.jupiter.api", "Test")

    val testClass = TypeSpec.classBuilder("QueryParametersGeneratedTests")

    (2..10).forEach { i ->
        val functionBuilder = FunSpec.builder("parameters")
            .receiver(handlerDsl)

        (1..i).forEach {
            val typeT = TypeVariableName("T$it")
            val typeU = TypeVariableName("U$it")

            functionBuilder.addTypeVariable(typeT)
            functionBuilder.addTypeVariable(typeU)

            functionBuilder.addParameter("parameter$it", queryParam.parameterizedBy(typeT, typeU))
        }

        functionBuilder
            .addParameter(
                "init",
                LambdaTypeName.get(
                    receiver = handlerDsl,
                    parameters = *(1..i).map { TypeVariableName("T$it") }.toTypedArray(),
                    returnType = Unit::class.asClassName()
                )
            )
            .returns(Unit::class.asClassName())
            .addCode(
                """
                |this.parameters(%1L) { %2L ->
                |  this.parameter(parameter%3L) { v%3L ->
                |    init(%4L)
                |  }
                |}
                |""".trimMargin(),
                (1 until i).joinToString { "parameter$it" },
                (1 until i).joinToString { "v$it" },
                i,
                (1..i).joinToString { "v$it" }
            )

        fileBuilder.addFunction(functionBuilder.build())

        val testFunctionBuilder = FunSpec.builder("parameters should extract $i values")
            .addAnnotation(test)
            .addStatement(
                """
                |runHandlerTest(
                |    handler {
                |       parameters(${(1..i).map { "\"p$it\".intParam()" }.joinToString()}) { ${(1..i).map { "p$it" }.joinToString()} ->
                |           ok(Flux.fromIterable(listOf(${(1..i).map { "p$it" }.joinToString()})))
                |       }
                |    },
                |    {
                |        expectStatus().isOk
                |            .expectBodyList(Int::class.java).returnResult()
                |            .apply { assertThat(responseBody).containsExactly(${(1..i).map { "$it" }.joinToString()}) }
                |    },
                |    request = { get().uri("/test?${(1..i).map { "p$it=$it" }.joinToString("&")}") })
                """.trimMargin()
            )

        testClass.addFunction(testFunctionBuilder.build())
    }

    fileBuilder.build().writeTo(outputDir)

    FileSpec.builder("de.bebauer.webflux.handler.dsl", "QueryParametersGeneratedTests")
        .addImport("org.assertj.core.api", "Assertions.assertThat")
        .addImport("reactor.core.publisher", "Flux")
        .addType(testClass.build())
        .build()
        .writeTo(testOutDir)
}