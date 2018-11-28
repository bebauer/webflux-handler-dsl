package webflux.handler.dsl

data class PathVariable<T>(
    internal val name: String,
    internal val converter: (String) -> T
)

fun <T> String.pathVariable(converter: (String) -> T) = PathVariable(this, converter)

fun <T> HandlerDsl.pathVariable(
    variable: PathVariable<T>,
    init: HandlerDsl.(T) -> Unit
) {
    response = nest { request ->
        val value = request.pathVariable(variable.name)

        init(variable.converter(value))
    }
}

fun <T1> HandlerDsl.pathVariables(
    variable1: PathVariable<T1>,
    init: HandlerDsl.(T1) -> Unit
) = pathVariable(variable1, init)