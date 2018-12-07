# forbidden

## Signature

```kotlin
fun <T> HandlerDsl.forbidden(value: T?)

inline fun <reified T> HandlerDsl.forbidden(flux: Flux<T>)

inline fun <reified T> HandlerDsl.forbidden(mono: Mono<T>)

fun HandlerDsl.forbidden(
    init: ServerResponse.BodyBuilder.() -> Mono<ServerResponse>)

fun HandlerDsl.forbidden(inserter: BodyInserter<*, in ServerHttpResponse>)
```

## Description

Completes with HTTP status Forbidden (403).

## Examples

```kotlin
router {
    GET("/", handler {
        val items: Flux<Item> = repo.findItems()
    
        forbidden(items)
    })
}
```

```kotlin
router {
    GET("/", handler {
        forbidden {
            contentType(MediaType.APPLICATION_JSON)
            header("created-by", "xxx")
            body(fromObject("The Item"))
        }
    })
}
