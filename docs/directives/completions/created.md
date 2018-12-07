# created

## Signature

```kotlin
fun <T> HandlerDsl.created(value: T?)

inline fun <reified T> HandlerDsl.created(flux: Flux<T>)

inline fun <reified T> HandlerDsl.created(mono: Mono<T>)

fun HandlerDsl.created(
    init: ServerResponse.BodyBuilder.() -> Mono<ServerResponse>)

fun HandlerDsl.created(inserter: BodyInserter<*, in ServerHttpResponse>)
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
