# complete

## Signature

```kotlin
fun complete(response: Mono<ServerResponse>)

fun HandlerDsl.complete()

fun HandlerDsl.complete(status: HttpStatus)

fun <T> HandlerDsl.complete(value: T?)

fun <T> HandlerDsl.complete(status: HttpStatus, value: T?)

inline fun <reified T> HandlerDsl.complete(flux: Flux<T>)

inline fun <reified T> HandlerDsl.complete(mono: Mono<T>)

inline fun <reified T> HandlerDsl.complete(
    status: HttpStatus, 
    flux: Flux<T>)
    
inline fun <reified T> HandlerDsl.complete(
    status: HttpStatus, 
    mono: Mono<T>)

fun HandlerDsl.complete(
    init: ServerResponse.BodyBuilder.() -> Mono<ServerResponse>)

fun HandlerDsl.complete(
    status: HttpStatus,
    init: ServerResponse.BodyBuilder.() -> Mono<ServerResponse>
)

fun HandlerDsl.complete(inserter: BodyInserter<*, in ServerHttpResponse>)

fun HandlerDsl.complete(
    status: HttpStatus,
    inserter: BodyInserter<*, in ServerHttpResponse>
)

fun complete(result: Either<Throwable, Mono<ServerResponse>>)
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