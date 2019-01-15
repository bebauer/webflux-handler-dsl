# extractRequestBodyToMono

## Signature

```kotlin
inline fun <reified T> HandlerDsl.extractRequestBodyToMono(
    crossinline init: HandlerDsl.(Mono<T>) -> CompleteOperation)
    : CompleteOperation
```

## Description

Extracts the body from the `ServerRequest` into a typed `Mono`.

## Example

```kotlin
router {
    POST("/", handler {
        extractRequestBodyToMono<Entity> { body ->
            complete(body)
        }
    })
}
```