# onComplete

## Signature

```kotlin
fun <T> HandlerDsl.onComplete(
    future: Future<T>, 
    init: HandlerDsl.(Try<T>) -> Unit)

fun <T> HandlerDsl.onComplete(
    future: Future<T>, 
    timeout: Duration, 
    init: HandlerDsl.(Try<T>) -> Unit)

fun <T> HandlerDsl.onComplete(
    future: Future<T>, 
    timeout: Timeout, 
    init: HandlerDsl.(Try<T>) -> Unit)

fun <T> HandlerDsl.onComplete(
    future: Future<T>, 
    timeout: Option<Duration>, 
    init: HandlerDsl.(Try<T>) -> Unit)

fun <T> HandlerDsl.onComplete(
    future: Future<T>, 
    timeout: Option<Timeout>, 
    init: HandlerDsl.(Try<T>) -> Unit)
```

## Description

`onComplete` accepts a future and waits for it's value. The method allows several ways to define a timeout.
After the future completed the result is available in the nested block as a `Try`.

## Example

```kotlin
import de.bebauer.webflux.handler.dsl.time.seconds

router {
    GET("/", handler {
        onComplete(myFuture, 5.seconds) { result ->
            when(result) {
                is Try.Success -> complete(result.value)
                is Try.Failure -> failWith(result.exception)
            }
        }
    })
}
```