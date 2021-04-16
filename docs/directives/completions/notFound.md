# notFound

## Signature

```kotlin
fun <T> notFound(
    value: T?, 
    builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ValueCompleteOperation<T>

inline fun <reified T> notFound(
    flux: Flux<T>, 
    noinline builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ResponseBuilderCompleteOperation

inline fun <reified T> notFound(
    mono: Mono<T>, 
    noinline builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): MonoBodyCompleteOperation<T>

fun notFound(init: ServerResponse.BodyBuilder.() -> Mono<ServerResponse> = { build() }):
    ResponseBuilderCompleteOperation

fun notFound(
    inserter: BodyInserter<*, in ServerHttpResponse>, 
    builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ResponseBuilderCompleteOperation
```

## Description

Completes with HTTP status Not Found (404).

## Examples

```kotlin
router {
    GET("/", handler {
        val items: Flux<Item> = repo.findItems()
    
        notFound(items)
    })
}
```

```kotlin
router {
    GET("/", handler {
        notFound {
            contentType(MediaType.APPLICATION_JSON)
            header("created-by", "xxx")
            body(fromValue("The Item"))
        }
    })
}
