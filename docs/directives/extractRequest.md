# extractRequest

# Signature

```kotlin
fun extractRequest(init: HandlerDsl.(ServerRequest) -> Unit)
```

## Description

This directive extracts the request so it can be used directly.

## Example

```kotlin
router {
    GET("/{id}", handler {
        extractRequest { request ->
            val id = request.pathVariable("id")
            
            complete(repo.findById(id))
        }
    })
}
```