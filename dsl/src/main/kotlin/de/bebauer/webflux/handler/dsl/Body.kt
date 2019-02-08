package de.bebauer.webflux.handler.dsl

import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.reactive.function.BodyExtractor
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Extracts the request body with the [BodyExtractor].
 *
 * @param <T> the type of the body returned
 * @param extractor the {@code BodyExtractor} that reads from the request
 */
fun <T> HandlerDsl.extractRequestBody(
    extractor: BodyExtractor<T, in ServerHttpRequest>,
    init: HandlerDsl.(T) -> CompleteOperation
) =
    extractRequest { request ->
        init(request.body(extractor))
    }

/**
 * Extracts the request body with the [BodyExtractor] and hints.
 *
 * @param <T> the type of the body returned
 * @param extractor the {@code BodyExtractor} that reads from the request
 * @param hints the map of hints like {@link Jackson2CodecSupport#JSON_VIEW_HINT} to use to customize body extraction
 */
fun <T> HandlerDsl.extractRequestBody(
    extractor: BodyExtractor<T, in ServerHttpRequest>,
    hints: Map<String, Any>,
    init: HandlerDsl.(T) -> CompleteOperation
) = extractRequest { request ->
    init(request.body(extractor, hints))
}

/**
 * Extracts the body from the request an provides it as a [Mono].
 *
 * @param T the element type
 */
inline fun <reified T> HandlerDsl.extractRequestBodyToMono(crossinline init: HandlerDsl.(Mono<T>) -> CompleteOperation) =
    extractRequest { request ->
        init(request.bodyToMono(T::class.java))
    }

/**
 * Extracts the body from the request an provides it as a [Flux].
 *
 * @param T the element type
 */
inline fun <reified T> HandlerDsl.extractRequestBodyToFlux(crossinline init: HandlerDsl.(Flux<T>) -> CompleteOperation) =
    extractRequest { request ->
        init(request.bodyToFlux(T::class.java))
    }