# extractRequestBody

## Signature

```kotlin
fun <T> HandlerDsl.extractRequestBody(
    extractor: BodyExtractor<T, in ServerHttpRequest>, 
    init: HandlerDsl.(T) -> Unit)

fun <T> HandlerDsl.extractRequestBody(
    extractor: BodyExtractor<T, in ServerHttpRequest>,
    hints: Map<String, Any>,
    init: HandlerDsl.(T) -> Unit)
```

## Description

Extracts the body from the `ServerRequest` using the specified `BodyExtractor`.

## Example

```kotlin
router {
    POST("/", handler {
        extractRequestBody(BodyExtractors.toMono(Entity::class.java)) { body ->
            complete(body)
        }
    })
}
```