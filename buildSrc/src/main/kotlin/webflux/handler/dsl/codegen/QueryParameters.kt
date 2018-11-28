package webflux.handler.dsl.codegen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

import java.io.File

internal fun generateParameterDsl(outputDir: File) {
    val fileBuilder = FileSpec.builder("webflux.handler.dsl", "QueryParameters")

    val queryParam = ClassName("webflux.handler.dsl", "QueryParameter")
    val handlerDsl = ClassName("webflux.handler.dsl", "HandlerDsl")

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
                (1..i - 1).map { "parameter$it" }.joinToString(),
                (1..i - 1).map { "v$it" }.joinToString(),
                i,
                (1..i).map { "v$it" }.joinToString()
            )

        fileBuilder.addFunction(functionBuilder.build())
    }

    fileBuilder.build().writeTo(outputDir)
}