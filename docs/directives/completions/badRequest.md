# badRequest

## Signature

```kotlin
fun <T> HandlerDsl.badRequest(value: T?)

inline fun <reified T> HandlerDsl.badRequest(flux: Flux<T>)

inline fun <reified T> HandlerDsl.badRequest(mono: Mono<T>)

fun HandlerDsl.badRequest(
    init: ServerResponse.BodyBuilder.() -> Mono<ServerResponse>)

fun HandlerDsl.badRequest(inserter: BodyInserter<*, in ServerHttpResponse>)
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
