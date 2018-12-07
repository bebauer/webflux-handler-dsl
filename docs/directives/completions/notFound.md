# notFound

## Signature

```kotlin
fun <T> HandlerDsl.notFound(value: T?)

inline fun <reified T> HandlerDsl.notFound(flux: Flux<T>)

inline fun <reified T> HandlerDsl.notFound(mono: Mono<T>)

fun HandlerDsl.notFound(
    init: ServerResponse.BodyBuilder.() -> Mono<ServerResponse>)

fun HandlerDsl.notFound(inserter: BodyInserter<*, in ServerHttpResponse>)
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
            body(fromObject("The Item"))
        }
    })
}
