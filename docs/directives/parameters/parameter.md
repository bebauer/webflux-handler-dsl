# parameter

## Signature

```kotlin
fun <T, U> HandlerDsl.parameter(parameter: QueryParameter<T, U>, init: HandlerDsl.(T) -> Unit)
```

## Description

Extracts a query parameter from the `ServerRequest`. See [parameters](parameters.md) if more than one 
parameter should be extracted. See [Query Parameters](README.md) for a description of how to build `QueryParameter`
instances.

## Example

```kotlin
router {
    GET("/", handler {
        parameter("query".stringParam()) { query ->
            complete(repo.findByQuery(query))
        }
    })
}
```
