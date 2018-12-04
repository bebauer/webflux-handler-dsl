# cookie

## Signature

```kotlin
fun <T, U> HandlerDsl.cookie(
    cookie: CookieName<T, U>, 
    init: HandlerDsl.(T) -> Unit)
```

## Description

Extracts cookie values from a `ServerRequest`. See [Cookies](README.md) for how to build `CookieName` objects. 

## Examples

```kotlin
router {
    GET("/", handler {
        cookie("SESSION_ID".stringCookie().single()) { sessionId ->
            complete(repo.findSessionById(sessionId))
        }
    })
}
```
