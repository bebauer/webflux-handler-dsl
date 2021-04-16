package de.bebauer.webflux.handler.dsl.codegen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File

internal fun generatePathVariableDsl(outputDir: File, testOutDir: File) {
    val fileBuilder = FileSpec.builder("de.bebauer.webflux.handler.dsl", "PathVariablesGenerated")

    val pathVariable = ClassName("de.bebauer.webflux.handler.dsl", "PathVariable")
    val handlerDsl = ClassName("de.bebauer.webflux.handler.dsl", "HandlerDsl")
    val wordSpec = ClassName("io.kotest.core.spec.style", "WordSpec")
    val completeOperation = ClassName("de.bebauer.webflux.handler.dsl", "CompleteOperation")

    val tests = mutableListOf<String>()

    (2..20).forEach { i ->
        val functionBuilder = FunSpec.builder("pathVariables")
            .receiver(handlerDsl)

        (1..i).forEach {
            val typeT = TypeVariableName("T$it")
            val typeU = TypeVariableName("U$it")

            functionBuilder.addTypeVariable(typeT)
            functionBuilder.addTypeVariable(typeU)

            functionBuilder.addParameter("variable$it", pathVariable.parameterizedBy(typeT, typeU))
        }

        functionBuilder
            .addParameter(
                "init",
                LambdaTypeName.get(
                    receiver = handlerDsl,
                    returnType = completeOperation,
                    parameters = (1..i).map { TypeVariableName("T$it") }.toTypedArray(),
                )
            )
            .returns(completeOperation)
            .addCode(
                """
                |return this.pathVariables(%1L) { %2L ->
                |  this.pathVariable(variable%3L) { v%3L ->
                |    init(%4L)
                |  }
                |}
                |""".trimMargin(),
                (1 until i).joinToString { "variable$it" },
                (1 until i).joinToString { "v$it" },
                i,
                (1..i).joinToString { "v$it" }
            )

        fileBuilder.addFunction(functionBuilder.build())

        tests.add(
            """
            |"extract $i values" {
            |   runHandlerTest(
            |       handler {
            |          pathVariables(${(1..i).joinToString { "\"p$it\".intVar" }}) { ${
                (1..i).joinToString { "p$it" }
            } ->
            |              ok(Flux.fromIterable(listOf(${(1..i).joinToString { "p$it" }})))
            |          }
            |       },
            |       {
            |           expectStatus().isOk
            |               .expectBodyList(Int::class.java)
            |               .returnResult().responseBody should containExactly(${(1..i).joinToString { "$it" }})
            |       },
            |       route = { GET("/test/${(1..i).joinToString("/") { "{p$it}" }}", it) },
            |       request = { get().uri("/test/${(1..i).joinToString("/")}") })
            |}
            """.trimMargin()
        )
    }

    fileBuilder.build().writeTo(outputDir)

    FileSpec.builder("de.bebauer.webflux.handler.dsl", "PathVariablesGeneratedTests")
        .addImport("io.kotest.matchers", "should")
        .addImport("io.kotest.matchers.collections", "containExactly")
        .addImport("reactor.core.publisher", "Flux")
        .addType(
            TypeSpec.classBuilder("PathVariablesGeneratedTests")
                .superclass(wordSpec).addInitializerBlock(
                    CodeBlock.of(
                        """
                        |"pathVariables" should {
                        |   ${tests.joinToString("\n\n")}
                        |}
                        |""".trimMargin()
                    )
                ).build()
        )
        .build()
        .writeTo(testOutDir)
}