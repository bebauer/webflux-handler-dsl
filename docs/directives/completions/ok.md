# ok

## Signature

```kotlin
fun <T> ok(
    value: T?, 
    builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ValueCompleteOperation<T>

inline fun <reified T> ok(
    flux: Flux<T>, 
    noinline builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ResponseBuilderCompleteOperation

inline fun <reified T> ok(
    mono: Mono<T>, 
    noinline builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): MonoBodyCompleteOperation<T>

fun ok(init: ServerResponse.BodyBuilder.() -> Mono<ServerResponse> = { build() }):
    ResponseBuilderCompleteOperation

fun ok(
    inserter: BodyInserter<*, in ServerHttpResponse>, 
    builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ResponseBuilderCompleteOperation
```

## Description

Completes with HTTP status OK (200).

## Examples

```kotlin
router {
    GET("/", handler {
        val items: Flux<Item> = repo.findItems()
    
        ok(items)
    })
}
```

```kotlin
router {
    GET("/", handler {
        ok {
            contentType(MediaType.APPLICATION_JSON)
            header("created-by", "xxx")
            body(fromObject("The Item"))
        }
    })
}
