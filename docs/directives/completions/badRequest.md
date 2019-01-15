# badRequest

## Signature

```kotlin
fun <T> badRequest(
    value: T?, 
    builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ValueCompleteOperation<T>

inline fun <reified T> badRequest(
    flux: Flux<T>, 
    noinline builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ResponseBuilderCompleteOperation

inline fun <reified T> badRequest(
    mono: Mono<T>, 
    noinline builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): MonoBodyCompleteOperation<T>

fun badRequest(init: ServerResponse.BodyBuilder.() -> Mono<ServerResponse> = { build() }):
    ResponseBuilderCompleteOperation

fun badRequest(
    inserter: BodyInserter<*, in ServerHttpResponse>, 
    builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ResponseBuilderCompleteOperation
```

## Description

Completes with HTTP status Bad Request (400).

## Examples

```kotlin
router {
    GET("/", handler {
        val items: Flux<Item> = repo.findItems()
    
        badRequest(items)
    })
}
```

```kotlin
router {
    GET("/", handler {
        badRequest {
            contentType(MediaType.APPLICATION_JSON)
            header("created-by", "xxx")
            body(fromObject("The Item"))
        }
    })
}
