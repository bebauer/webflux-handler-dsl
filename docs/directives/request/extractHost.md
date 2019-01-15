# extractHost

## Signature

```kotlin
fun HandlerDsl.extractHost(init: HandlerDsl.(String) -> CompleteOperation)
    : CompleteOperation
```

## Description

Extracts the host from the `ServerRequest` URI.

## Example

```kotlin
router {
    GET("/", handler {
        extractHost { host ->
            // do something with the host
            
            complete("ok")
        }
    })
}
```