package webflux.handler.dsl

import org.reactivestreams.Publisher
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

inline fun <reified T> HandlerDsl.complete(provider: Publisher<T>) = complete {
    body(provider, T::class.java)
}

fun HandlerDsl.complete(init: ServerResponse.BodyBuilder.() -> Mono<out ServerResponse>) {
    val response = ServerResponse.ok()

    complete(response.init())
}

fun HandlerDsl.complete() = complete { build() }

fun HandlerDsl.complete(status: HttpStatus,
                        init: ServerResponse.BodyBuilder.() -> Mono<out ServerResponse>) {
    val response = ServerResponse.status(status)

    complete(response.init())
}

fun <T> HandlerDsl.complete(value: T) = complete { body(fromObject(value)) }

fun HandlerDsl.complete(status: HttpStatus) = complete(status) { build() }

fun <T> HandlerDsl.complete(status: HttpStatus, value: T) = complete(status) {
    body(fromObject(value))
}

inline fun <reified T> HandlerDsl.complete(status: HttpStatus, provider: Publisher<T>) = complete(
        status) {
    body(provider, T::class.java)
}

fun HandlerDsl.complete(inserter: BodyInserter<*, in ServerHttpResponse>) = complete {
    body(inserter)
}

fun HandlerDsl.complete(status: HttpStatus,
                        inserter: BodyInserter<*, in ServerHttpResponse>) = complete(status) {
    body(inserter)
}