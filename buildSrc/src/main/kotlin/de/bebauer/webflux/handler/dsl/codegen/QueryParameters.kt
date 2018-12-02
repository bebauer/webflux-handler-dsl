package de.bebauer.webflux.handler.dsl.codegen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

import java.io.File

internal fun generateParameterDsl(outputDir: File) {
    val fileBuilder = FileSpec.builder("de.bebauer.webflux.handler.dsl", "QueryParametersGenerated")

    val queryParam = ClassName("de.bebauer.webflux.handler.dsl", "QueryParameter")
    val handlerDsl = ClassName("de.bebauer.webflux.handler.dsl", "HandlerDsl")

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
    }

    fileBuilder.build().writeTo(outputDir)
}