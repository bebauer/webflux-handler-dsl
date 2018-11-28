package webflux.handler.dsl

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

data class QueryParameter<T, U>(
    internal val name: String,
    internal val converter: (String) -> U?,
    internal val optional: Boolean = false,
    internal val defaultValue: T? = null,
    internal val repeated: Boolean = false
)

fun <T> String.queryParam(converter: (String) -> T): QueryParameter<T, T> = QueryParameter(this, converter)

fun <T, U> QueryParameter<T, U>.optional(defaultValue: T? = null): QueryParameter<T?, U> =
    QueryParameter(this.name, this.converter, true, defaultValue, this.repeated)

fun <T> QueryParameter<T, T>.repeated(): QueryParameter<List<T>, T> =
    QueryParameter(this.name, this.converter, this.optional, repeated = true)

fun String.stringParam() = this.queryParam { it }

fun String.intParam() = this.queryParam(String::toInt)

fun <T> String.csvParam(converter: (String) -> T): QueryParameter<List<T>, List<T>> = this.queryParam { value ->
    value.split(",").map(converter)
}

fun String.csvParam() = this.csvParam { it }

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

//fun <T1, U1, T2, U2> HandlerDsl.parameters(
//    parameter1: QueryParameter<T1, U1>,
//    parameter2: QueryParameter<T2, U2>,
//    init: HandlerDsl.(T1, T2) -> Unit
//) = this.parameters(parameter1) { v1 ->
//    this.parameter(parameter2) { v2 ->
//        init(v1, v2)
//    }
//}
//
//fun <T1, U1, T2, U2, T3, U3> HandlerDsl.parameters(
//    parameter1: QueryParameter<T1, U1>,
//    parameter2: QueryParameter<T2, U2>,
//    parameter3: QueryParameter<T3, U3>,
//    init: HandlerDsl.(T1, T2, T3) -> Unit
//) = this.parameters(parameter1, parameter2) { v1, v2 ->
//    this.parameter(parameter3) { v3 ->
//        init(v1, v2, v3)
//    }
//}
//
//fun <T1, U1, T2, U2, T3, U3, T4, U4> HandlerDsl.parameters(
//    parameter1: QueryParameter<T1, U1>,
//    parameter2: QueryParameter<T2, U2>,
//    parameter3: QueryParameter<T3, U3>,
//    parameter4: QueryParameter<T4, U4>,
//    init: HandlerDsl.(T1, T2, T3, T4) -> Unit
//) = this.parameters(parameter1, parameter2, parameter3) { v1, v2, v3 ->
//    this.parameter(parameter4) { v4 ->
//        init(v1, v2, v3, v4)
//    }
//}
//
//fun <T1, U1, T2, U2, T3, U3, T4, U4, T5, U5> HandlerDsl.parameters(
//    parameter1: QueryParameter<T1, U1>,
//    parameter2: QueryParameter<T2, U2>,
//    parameter3: QueryParameter<T3, U3>,
//    parameter4: QueryParameter<T4, U4>,
//    parameter5: QueryParameter<T5, U5>,
//    init: HandlerDsl.(T1, T2, T3, T4, T5) -> Unit
//) = this.parameters(parameter1, parameter2, parameter3, parameter4) { v1, v2, v3, v4 ->
//    this.parameter(parameter5) { v5 ->
//        init(v1, v2, v3, v4, v5)
//    }
//}
