package webflux.handler.dsl

import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

fun runHandlerTest(
    handler: (ServerRequest) -> Mono<out ServerResponse>,
    validation: WebTestClient.ResponseSpec.() -> Unit,
    route : RouterFunctionDsl.((ServerRequest) -> Mono<out ServerResponse>) -> Unit = { GET("/test", it) },
    request: WebTestClient.() -> WebTestClient.RequestHeadersSpec<*> = { get().uri("/test") }
) {
    val router = router {
        route(handler)
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