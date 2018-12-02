# failWith

## Signature

```kotlin
fun failWith(throwable: Throwable)
```

Fails with the specified exception.

```kotlin
fun failWith(message: String)
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