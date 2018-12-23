package de.bebauer.webflux.handler.dsl

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

interface CompleteOperation {
    val response: Mono<ServerResponse>
}

class MonoBodyCompleteOperation<T>(
    private val status: HttpStatus,
    private val mono: Mono<T>,
    private val elementClass: Class<T>,
    private val builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
) : CompleteOperation {
    private var alternative: Option<CompleteOperation> = None

    override val response: Mono<ServerResponse>
        get() = alternative.flatMap { alt ->
            Some(mono.flatMap {
                ServerResponse.status(status).builderInit().body(Mono.just(it), elementClass)
            }.switchIfEmpty(Mono.defer { alt.response }))
        }.getOrElse {
            ServerResponse.status(status).builderInit().body(mono, elementClass)
        }

    infix fun <T : CompleteOperation> or(other: T): T {
        alternative = Some(other)
        return other
    }
}

class ValueCompleteOperation<T>(
    private val status: HttpStatus,
    private val maybeValue: Option<T>,
    private val builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
) : CompleteOperation {
    private var alternative: Option<CompleteOperation> = None

    override val response: Mono<ServerResponse>
        get() = maybeValue.map { ServerResponse.status(status).builderInit().body(BodyInserters.fromObject(it)) }
            .getOrElse {
                alternative.map { it.response }.getOrElse { ServerResponse.status(status).builderInit().build() }
            }

    infix fun <T : CompleteOperation> or(other: T): T {
        alternative = Some(other)
        return other
    }
}

class FinalCompleteOperation(private val responseBuilder: () -> Mono<ServerResponse>) : CompleteOperation {
    override val response: Mono<ServerResponse>
        get() = responseBuilder()
}

class ResponseCompleteOperation(override val response: Mono<ServerResponse>) : CompleteOperation