# complete

## Signature

```kotlin
fun complete(response: Mono<out ServerResponse>)

fun HandlerDsl.complete()

fun HandlerDsl.complete(status: HttpStatus)

fun <T> HandlerDsl.complete(value: T)

fun <T> HandlerDsl.complete(status: HttpStatus, value: T)

inline fun <reified T> HandlerDsl.complete(publisher: Publisher<T>)

inline fun <reified T> HandlerDsl.complete(
    status: HttpStatus, 
    publisher: Publisher<T>)

fun HandlerDsl.complete(
    init: ServerResponse.BodyBuilder.() -> Mono<out ServerResponse>)

fun HandlerDsl.complete(
    status: HttpStatus,
    init: ServerResponse.BodyBuilder.() -> Mono<out ServerResponse>
)

fun HandlerDsl.complete(inserter: BodyInserter<*, in ServerHttpResponse>)

fun HandlerDsl.complete(
    status: HttpStatus,
    inserter: BodyInserter<*, in ServerHttpResponse>
)
```

## Examples

```kotlin
router {
    GET("/", handler {
        val items: Flux<Item> = repo.findItems()
    
        complete(items)
    })
}
```

```kotlin
router {
    GET("/", handler {
        complete(HttpStatus.CREATED) {
            contentType(MediaType.APPLICATION_JSON)
            header("created-by", "xxx")
            body(fromObject("The Item"))
        }
    })
}
```