# pathVariables

## Signature

```kotlin
fun <T1, U1> HandlerDsl.pathVariables(
    variable1: PathVariable<T1, U1>, 
    init: HandlerDsl.(T1) -> CompleteOperation)
    : CompleteOperation

fun <T1, U1, T2, U2> HandlerDsl.pathVariables(
    variable1: PathVariable<T1, U1>, 
    variable2: PathVariable<T2, U2>, 
    init: HandlerDsl.(T1, T2) -> CompleteOperation)
    : CompleteOperation

...

fun <T1, U1, ..., T??, U??> HandlerDsl.pathVariables(
    variable1: PathVariable<T1, U1>, 
    ..., 
    variable??: PathVariable<T??, U??>, 
    init: HandlerDsl.(T1, ..., T??) -> CompleteOperation)
    : CompleteOperation
```

## Description

Extracts path variables from the `ServerRequest`.

## Example

```kotlin
router {
    GET("/topics/{tid}/posts/{pid}", handler {
        pathVariables("tid".intVar, "pid".stringVar) { tid, pid ->
            complete(repo.findByTopicAndPostId(tid, pid))
        }
    })
}
```

