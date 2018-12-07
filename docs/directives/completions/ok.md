# ok

## Signature

```kotlin
fun <T> HandlerDsl.ok(value: T?)

inline fun <reified T> HandlerDsl.ok(flux: Flux<T>)

inline fun <reified T> HandlerDsl.ok(mono: Mono<T>)

fun HandlerDsl.ok(
    init: ServerResponse.BodyBuilder.() -> Mono<ServerResponse>)

fun HandlerDsl.ok(inserter: BodyInserter<*, in ServerHttpResponse>)
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
