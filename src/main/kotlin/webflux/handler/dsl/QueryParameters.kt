package webflux.handler.dsl

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

data class QueryParameter<T, U>(
    internal val name: String,
    internal val converter: (String) -> U,
    internal val optional: Boolean = false,
    internal val defaultValue: T? = null,
    internal val repeated: Boolean = false
)

fun <T> String.queryParam(converter: (String) -> T): QueryParameter<T, T> = QueryParameter(this, converter)

@Suppress("UNCHECKED_CAST")
fun <T, U> HandlerDsl.parameter(
    parameter: QueryParameter<T, U>,
    init: HandlerDsl.(T) -> Unit
) {
    response = nest { request ->
        val params = request.queryParams()
        val values = params[parameter.name]

        val value = if (values != null) {
            when {
                parameter.repeated -> values.map(parameter.converter) as T
                else -> parameter.converter(values[0]) as T
            }
        } else {
            when {
                parameter.optional -> parameter.defaultValue as T
                else -> throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Missing required query parameter ${parameter.name}."
                )
            }
        }

        init(value)
    }
}

fun <T1, U1> HandlerDsl.parameters(
    parameter1: QueryParameter<T1, U1>,
    init: HandlerDsl.(T1) -> Unit
) = this.parameter(parameter1, init)
