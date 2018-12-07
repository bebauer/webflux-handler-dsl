# unauthorized

## Signature

```kotlin
fun <T> HandlerDsl.unauthorized(value: T?)

inline fun <reified T> HandlerDsl.unauthorized(flux: Flux<T>)

inline fun <reified T> HandlerDsl.unauthorized(mono: Mono<T>)

fun HandlerDsl.unauthorized(
    init: ServerResponse.BodyBuilder.() -> Mono<ServerResponse>)

fun HandlerDsl.unauthorized(inserter: BodyInserter<*, in ServerHttpResponse>)
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
