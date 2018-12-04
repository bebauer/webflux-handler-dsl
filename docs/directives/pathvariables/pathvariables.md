# pathVariables

## Signature

```kotlin
fun <T1> HandlerDsl.pathVariables(
    variable1: PathVariable<T1>, 
    init: HandlerDsl.(T1) -> Unit)

fun <T1, T2> HandlerDsl.pathVariables(
    variable1: PathVariable<T1>, 
    variable2: PathVariable<T2>, 
    init: HandlerDsl.(T1, T2) -> Unit)

...

fun <T1, ..., T10> HandlerDsl.pathVariables(
    variable1: PathVariable<T1>, 
    ..., 
    variable10: PathVariable<T10>, 
    init: HandlerDsl.(T1, ..., T10) -> Unit)
```

## Description

Extracts path variables from the `ServerRequest`.

## Example

```kotlin
router {
    GET("/topics/{tid}/posts/{pid}", handler {
        pathVariables("tid".intVar(), "pid".stringVar()) { tid, pid ->
            complete(repo.findByTopicAndPostId(tid, pid))
        }
    })
}
```

