package webflux.handler.dsl

import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.toOption
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

data class QueryParameter<T, U>(internal val name: String,
                                internal val converter: (String) -> U,
                                internal val optional: Boolean = false,
                                internal val defaultValue: T? = null,
                                internal val repeated: Boolean = false)

fun <T> String.queryParam(converter: (String) -> T): QueryParameter<T, T> = QueryParameter(this,
                                                                                           converter)

fun <T, U> QueryParameter<T, U>.optional(defaultValue: T? = null): QueryParameter<Option<T>, U> =
        QueryParameter(this.name, this.converter, true, defaultValue.toOption(), this.repeated)

fun <T> QueryParameter<T, T>.repeated(): QueryParameter<List<T>, T> =
        QueryParameter(this.name, this.converter, this.optional, repeated = true)

fun String.stringParam() = this.queryParam { it }

fun String.intParam() = this.queryParam(String::toInt)

fun <T> String.csvParam(converter: (String) -> T): QueryParameter<List<T>, List<T>> = this.queryParam { value ->
    value.split(",").map(converter)
}

fun String.csvParam() = this.csvParam { it }

@Suppress("UNCHECKED_CAST")
fun <T, U> HandlerDsl.parameter(parameter: QueryParameter<T, U>,
                                init: HandlerDsl.(T) -> Unit) = nest { request ->
    val params = request.queryParams()
    val values = params[parameter.name].toOption()

    @Suppress("IMPLICIT_CAST_TO_ANY") val value = values
            .map {
                when {
                    parameter.repeated -> it.map(parameter.converter)
                    else               -> parameter.converter(it[0])
                }
            }
            .map {
                when (parameter.optional) {
                    true  -> it.toOption()
                    false -> it
                }
            }
            .getOrElse {
                when {
                    parameter.optional -> parameter.defaultValue
                    else               -> throw ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Missing required query parameter ${parameter.name}."
                    )
                }
            } as T


    init(value)
}


fun <T1, U1> HandlerDsl.parameters(parameter1: QueryParameter<T1, U1>,
                                   init: HandlerDsl.(T1) -> Unit) = this.parameter(parameter1, init)
