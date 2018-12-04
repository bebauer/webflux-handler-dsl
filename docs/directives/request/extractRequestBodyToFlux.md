# extractRequestBodyToFlux

## Signature

```kotlin
inline fun <reified T> HandlerDsl.extractRequestBodyToFlux(
    crossinline init: HandlerDsl.(Flux<T>) -> Unit)
```

## Description

Extracts the body from the `ServerRequest` into a typed `Flux`.

## Example

```kotlin
router {
    POST("/", handler {
        extractRequestBodyToFlux<Entity> { body ->
            complete(body)
        }
    })
}
```