package webflux.handler.dsl

data class PathVariable<T>(internal val name: String, internal val converter: (String) -> T)

fun <T> String.pathVariable(converter: (String) -> T) = PathVariable(this, converter)

fun String.stringVar() = this.pathVariable { it }

fun String.intVar() = this.pathVariable(String::toInt)

fun <T> HandlerDsl.pathVariable(variable: PathVariable<T>,
                                init: HandlerDsl.(T) -> Unit) = nest { request ->
    val (name, converter) = variable

    val value = request.pathVariable(name)

    init(converter(value))
}


fun <T1> HandlerDsl.pathVariables(variable1: PathVariable<T1>,
                                  init: HandlerDsl.(T1) -> Unit) = pathVariable(variable1, init)