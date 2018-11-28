package webflux.handler.dsl

import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

fun handler(init: HandlerDsl.() -> Unit): (ServerRequest) -> Mono<out ServerResponse> = {
    HandlerDsl(it, init).invoke()
}

typealias Response = (ServerRequest) -> Mono<out ServerResponse>

open class HandlerDsl(
    private val request: ServerRequest,
    private val init: HandlerDsl.() -> Unit
) : () -> Mono<out ServerResponse> {

    var response: Response =
        { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(fromObject("Incomplete DSL")) }

    fun respond(f: Response) {
        response = f
    }

    fun nest(init: HandlerDsl.(ServerRequest) -> Unit): Response = { request ->
        HandlerDsl(request) {
            init(request)
        }.invoke()
    }

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

    fun String.stringVar() = this.pathVariable { it }

    fun String.intVar() = this.pathVariable(String::toInt)

    override fun invoke(): Mono<out ServerResponse> {
        init()

        return response(request)
    }
}