# pathVariables

## Signature

```kotlin
fun <T1> HandlerDsl.pathVariables(
    variable1: PathVariable<T1>, 
    init: HandlerDsl.(T1) -> CompleteOperation)
    : CompleteOperation

fun <T1, T2> HandlerDsl.pathVariables(
    variable1: PathVariable<T1>, 
    variable2: PathVariable<T2>, 
    init: HandlerDsl.(T1, T2) -> CompleteOperation)
    : CompleteOperation

...

fun <T1, ..., T??> HandlerDsl.pathVariables(
    variable1: PathVariable<T1>, 
    ..., 
    variable??: PathVariable<T??>, 
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

