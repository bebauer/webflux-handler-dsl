# failWith

## Signature

```kotlin
fun failWith(throwable: Throwable): ResponseCompleteOperation
```

Fails with the specified exception.

```kotlin
fun failWith(message: String): ResponseCompleteOperation
```

Fails with an `ResponseStatusException` with status code `500` and the specified message.

## Example

```kotlin
router {
    GET("/", handler {
        failWith("Not yet implemented!")
    })
}
```