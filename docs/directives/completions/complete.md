# complete

## Signature

```kotlin
fun complete(response: Mono<ServerResponse>): ResponseCompleteOperation

inline fun <reified T> complete(
    status: HttpStatus,
    flux: Flux<T>,
    noinline builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ResponseBuilderCompleteOperation

inline fun <reified T> complete(
    status: HttpStatus,
    mono: Mono<T>,
    noinline builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): MonoBodyCompleteOperation<T>

fun complete(
    status: HttpStatus,
    init: ServerResponse.BodyBuilder.() -> Mono<ServerResponse>
): ResponseBuilderCompleteOperation

fun <T> complete(
    status: HttpStatus,
    value: T?,
    builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ValueCompleteOperation<T>

fun complete(status: HttpStatus): ResponseBuilderCompleteOperation

fun complete(
    status: HttpStatus,
    inserter: BodyInserter<*, in ServerHttpResponse>,
    builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ResponseBuilderCompleteOperation

fun complete(operation: Mono<out CompleteOperation>): NestedCompleteOperation

fun Mono<out CompleteOperation>.toCompleteOperation(): NestedCompleteOperation
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
            body(fromValue("The Item"))
        }
    })
}
```

```kotlin
router {
    GET("/", handler {
        fetchSomeMono().map { 
            complete(HttpStatus.OK, it)
        }
        .switchIfEmpty(Mono.defer { notFound().toMono() })
        .toCompleteOperation()
    })
}
```