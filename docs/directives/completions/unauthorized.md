# unauthorized

## Signature

```kotlin
fun <T> unauthorized(
    value: T?, 
    builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ValueCompleteOperation<T>

inline fun <reified T> unauthorized(
    flux: Flux<T>, 
    noinline builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ResponseBuilderCompleteOperation

inline fun <reified T> unauthorized(
    mono: Mono<T>, 
    noinline builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): MonoBodyCompleteOperation<T>

fun unauthorized(init: ServerResponse.BodyBuilder.() -> Mono<ServerResponse> = { build() }):
    ResponseBuilderCompleteOperation

fun unauthorized(
    inserter: BodyInserter<*, in ServerHttpResponse>, 
    builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ResponseBuilderCompleteOperation
```

## Description

Completes with HTTP status Unauthorized (401).

## Examples

```kotlin
router {
    GET("/", handler {
        val items: Flux<Item> = repo.findItems()
    
        unauthorized(items)
    })
}
```

```kotlin
router {
    GET("/", handler {
        unauthorized {
            contentType(MediaType.APPLICATION_JSON)
            header("created-by", "xxx")
            body(fromObject("The Item"))
        }
    })
}
