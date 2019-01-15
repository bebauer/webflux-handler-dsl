# onSuccess

## Signature

```kotlin
fun <T> HandlerDsl.onSuccess(
    future: CompletableFuture<T>, 
    init: HandlerDsl.(T) -> CompleteOperation)
    : ResponseCompleteOperation

fun <T> HandlerDsl.onSuccess(
    future: CompletableFuture<T>, 
    timeout: Duration, 
    init: HandlerDsl.(T) -> CompleteOperation)
    : ResponseCompleteOperation

fun <T> HandlerDsl.onSuccess(
    future: CompletableFuture<T>, 
    timeout: Timeout, 
    init: HandlerDsl.(T) -> CompleteOperation)
    : ResponseCompleteOperation

fun <T> HandlerDsl.onSuccess(
    future: CompletableFuture<T>, 
    timeout: Option<Duration>, 
    init: HandlerDsl.(T) -> CompleteOperation)
    : ResponseCompleteOperation

fun <T> HandlerDsl.onSuccess(
    future: CompletableFuture<T>, 
    timeout: Option<Timeout>, 
    init: HandlerDsl.(T) -> CompleteOperation)
    : ResponseCompleteOperation
```

## Description

`onSuccess` accepts a future and waits for it's value. The method allows several ways to define a timeout.
After the future completed the result is available in the nested block. If the future fails or times out,
the whole handler will be completed with a failure, resulting in a `500` status code.

## Example

```kotlin
import de.bebauer.webflux.handler.dsl.time.seconds

router {
    GET("/", handler {
        onSuccess(myFuture, 5.seconds) { result ->
            complete(result)
        }
    })
}
```