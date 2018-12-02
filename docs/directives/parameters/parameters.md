# parameters

# parameter

## Signature

```kotlin
fun <T1, U1> HandlerDsl.parameters(
    parameter1: QueryParameter<T1, U1>, 
    init: HandlerDsl.(T1) -> Unit)

fun <T1, U1, T2, U2> HandlerDsl.parameters(
    parameter1: QueryParameter<T1, U1>,
    parameter2: QueryParameter<T2, U2>, 
    init: HandlerDsl.(T1, T2) -> Unit)

...

fun <T1, U1, ..., T10, U10> HandlerDsl.parameters(
    parameter1: QueryParameter<T1, U1>,
    ...,
    parameter10: QueryParameter<T10, U10>, 
    init: HandlerDsl.(T1, ..., T10) -> Unit)
```

## Description

Extracts up to ten query parameters from the `ServerRequest`. 
See [Query Parameters](README.md) for a description of how to build `QueryParameter` instances.

## Example

```kotlin
router {
    GET("/", handler {
        parameters(
            "query".stringParam(), 
            "from".intParam().optional(0), 
            "size".intParam().optional(10)) { query, from, size ->
            complete(repo.findByQuery(query, from, size))
        }
    })
}
```
