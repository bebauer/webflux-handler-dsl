# internalServerError

## Signature

```kotlin
fun <T> HandlerDsl.internalServerError(value: T?)

inline fun <reified T> HandlerDsl.internalServerError(flux: Flux<T>)

inline fun <reified T> HandlerDsl.internalServerError(mono: Mono<T>)

fun HandlerDsl.internalServerError(
    init: ServerResponse.BodyBuilder.() -> Mono<ServerResponse>)

fun HandlerDsl.internalServerError(inserter: BodyInserter<*, in ServerHttpResponse>)
```

## Description

Completes with HTTP status Internal Server Error (500).

## Examples

```kotlin
router {
    GET("/", handler {
        val items: Flux<Item> = repo.findItems()
    
        internalServerError(items)
    })
}
```

```kotlin
router {
    GET("/", handler {
        internalServerError {
            contentType(MediaType.APPLICATION_JSON)
            header("created-by", "xxx")
            body(fromObject("The Item"))
        }
    })
}
