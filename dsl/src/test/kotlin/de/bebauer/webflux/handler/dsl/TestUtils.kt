package de.bebauer.webflux.handler.dsl

import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

fun runHandlerTest(
    handler: (ServerRequest) -> Mono<ServerResponse>,
    validation: WebTestClient.ResponseSpec.() -> Unit,
    route: RouterFunctionDsl.((ServerRequest) -> Mono<ServerResponse>) -> Unit = { GET("/test", it) },
    request: WebTestClient.() -> WebTestClient.RequestHeadersSpec<*> = { get().uri("/test") }
) {
    val router = router {
        route(de.bebauer.webflux.handler.dsl.handler {
            extractRequest { request ->
                try {
                    complete(handler(request).doOnError { t -> println(t) })
                } catch (t: Throwable) {
                    println(t)

                    throw t
                }
            }
        })
    }

    validation(
        WebTestClient.bindToRouterFunction(router)
            .configureClient()
            .baseUrl("http://localhost")
            .build()
            .request()
            .exchange()
    )
}