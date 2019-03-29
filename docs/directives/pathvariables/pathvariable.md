# pathVariable

## Signature

```kotlin
fun <T, U> HandlerDsl.pathVariable(
    variable: PathVariable<T, U>, 
    init: HandlerDsl.(T) -> CompleteOperation)
    : CompleteOperation
```

## Description

Extracts a path variable from the `ServerRequest`. See [pathVariables](pathvariables.md) if more than one variable should be extracted.

## Example

```kotlin
router {
    GET("/{id}", handler {
        pathVariable("id".intVar) { id ->
            complete(repo.findById(id))
        }
    })
}
```

