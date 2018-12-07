package de.bebauer.webflux.handler.dsl.codegen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File

internal fun generateStatusCompletions(outputDir: File) {
    val fileBuilder = FileSpec.builder("de.bebauer.webflux.handler.dsl", "StatusCompletionsGenerated")

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

    fun generateWithBuilder(status: String) {
        fileBuilder.addFunction(
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
    }

    fun generateWithFlux(status: String) {
        val typeT = TypeVariableName("T")

        fileBuilder.addFunction(
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
    }

    fun generateWithMono(status: String) {
        val typeT = TypeVariableName("T")

        fileBuilder.addFunction(
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
    }

    fun generateWithValue(status: String) {
        val typeT = TypeVariableName("T")

        fileBuilder.addFunction(
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
    }

    fun generateWithBodyInserter(status: String) {
        fileBuilder.addFunction(
            FunSpec.builder(status.underscoreToCamelCase())
                .receiver(handlerDsl)
                .addParameter(
                    "inserter",
                    bodyInserter.parameterizedBy(
                        STAR,
                        TypeVariableName(serverHttpResponse.canonicalName, KModifier.IN)
                    )
                )
                .returns(completeOperation)
                .addStatement("return complete(%T.$status, inserter)", httpStatus)
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

    fileBuilder.build().writeTo(outputDir)
}

private fun String.underscoreToCamelCase() = "_([a-z\\d])".toRegex().replace(this.toLowerCase()) { m ->
    m.groups[1]?.value?.toUpperCase()!!
}