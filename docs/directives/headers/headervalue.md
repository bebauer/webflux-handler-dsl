# headerValue

## Signature

```kotlin
fun <T, U> HandlerDsl.headerValue(
    header: HeaderName<T, U>,
    init: HandlerDsl.(T) -> CompleteOperation)
    : CompleteOperation
```

## Description

Extracts a value from the request headers. See [Headers](README.md) for how to build `HeaderName` objects. 
The `de.bebauer.webflux.handler.dsl.Headers` object contains constants for all common headers.

## Examples

```kotlin
// with constant
router {
    GET("/", handler {
        headerValue(Headers.Location.single) { location ->
            complete(repo.findByLocation(location))
        }
    })
}

// without constant
router {
    GET("/", handler {
        headerValue("location".stringHeader.single) { location ->
            complete(repo.findByLocation(location))
        }
    })
}
```
