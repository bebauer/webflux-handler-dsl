# parameters

# parameter

## Signature

```kotlin
fun <T1, U1> HandlerDsl.parameters(
    parameter1: QueryParameter<T1, U1>, 
    init: HandlerDsl.(T1) -> CompleteOperation)
    : CompleteOperation

fun <T1, U1, T2, U2> HandlerDsl.parameters(
    parameter1: QueryParameter<T1, U1>,
    parameter2: QueryParameter<T2, U2>, 
    init: HandlerDsl.(T1, T2) -> CompleteOperation)
    : CompleteOperation

...

fun <T1, U1, ..., T??, U??> HandlerDsl.parameters(
    parameter1: QueryParameter<T1, U1>,
    ...,
    parameter??: QueryParameter<T??, U??>, 
    init: HandlerDsl.(T1, ..., T??) -> CompleteOperation)
    : CompleteOperation
```

## Description

Extracts up to ten query parameters from the `ServerRequest`. 
See [Query Parameters](README.md) for a description of how to build `QueryParameter` instances.

## Example

```kotlin
router {
    GET("/", handler {
        parameters(
            "query".stringParam, 
            "from".intParam.optional(0), 
            "size".intParam.optional(10)) { query, from, size ->
            complete(repo.findByQuery(query, from, size))
        }
    })
}
```
