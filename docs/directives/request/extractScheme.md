# extractScheme

## Signature

```kotlin
fun HandlerDsl.extractScheme(init: HandlerDsl.(String) -> Unit)
```

## Description

Extracts the scheme from the `ServerRequest` URI. E.g. `http` or `https`.

## Example

```kotlin
router {
    GET("/", handler {
        extractScheme { scheme ->
            // do something with the scheme
            
            complete("ok")
        }
    })
}
```