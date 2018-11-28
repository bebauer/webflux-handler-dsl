package webflux.handler.dsl.codegen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File

internal fun generatePathVariableDsl(outputDir: File) {
    val fileBuilder = FileSpec.builder("webflux.handler.dsl", "PathVariablesGenerated")

    val pathVariable = ClassName("webflux.handler.dsl", "PathVariable")
    val handlerDsl = ClassName("webflux.handler.dsl", "HandlerDsl")

    (2..10).forEach { i ->
        val functionBuilder = FunSpec.builder("pathVariables")
            .receiver(handlerDsl)

        (1..i).forEach {
            val typeT = TypeVariableName("T$it")

            functionBuilder.addTypeVariable(typeT)

            functionBuilder.addParameter("variable$it", pathVariable.parameterizedBy(typeT))
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
                |this.pathVariables(%1L) { %2L ->
                |  this.pathVariable(variable%3L) { v%3L ->
                |    init(%4L)
                |  }
                |}
                |""".trimMargin(),
                (1..i - 1).map { "variable$it" }.joinToString(),
                (1..i - 1).map { "v$it" }.joinToString(),
                i,
                (1..i).map { "v$it" }.joinToString()
            )

        fileBuilder.addFunction(functionBuilder.build())
    }

    fileBuilder.build().writeTo(outputDir)
}