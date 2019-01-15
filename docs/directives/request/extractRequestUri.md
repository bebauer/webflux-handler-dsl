# extractRequestUri

## Signature

```kotlin
fun HandlerDsl.extractRequestUri(init: HandlerDsl.(URI) -> CompleteOperation)
    : CompleteOperation
```

## Description

Extracts the URI from the `ServerRequest`.

## Example

```kotlin
router {
    GET("/", handler {
        extractRequestUri { uri ->
            // do something with the URI
            
            complete("ok")
        }
    })
}
```