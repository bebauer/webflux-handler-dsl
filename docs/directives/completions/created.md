# created

## Signature

```kotlin
fun <T> created(
    value: T?, 
    builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ValueCompleteOperation<T>

inline fun <reified T> created(
    flux: Flux<T>, 
    noinline builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ResponseBuilderCompleteOperation

inline fun <reified T> created(
    mono: Mono<T>, 
    noinline builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): MonoBodyCompleteOperation<T>

fun created(init: ServerResponse.BodyBuilder.() -> Mono<ServerResponse> = { build() }):
    ResponseBuilderCompleteOperation

fun created(
    inserter: BodyInserter<*, in ServerHttpResponse>, 
    builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ResponseBuilderCompleteOperation
```

## Description

Completes with HTTP status Created (201).

## Examples

```kotlin
router {
    GET("/", handler {
        val items: Flux<Item> = repo.findItems()
    
        created(items)
    })
}
```

```kotlin
router {
    GET("/", handler {
        created {
            contentType(MediaType.APPLICATION_JSON)
            header("created-by", "xxx")
            body(fromObject("The Item"))
        }
    })
}
