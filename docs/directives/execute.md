# execute

## Signature

```kotlin
fun execute(init: HandlerDsl.() -> Unit)
    : Either<Throwable, Mono<out ServerResponse>>
```

## Description

`execute` runs a handler DSL and return it's result.

## Example

```kotlin
router {
    GET("/", handler {
        val result = execute {
            complete("ok")
        }
        
        if (result is Either.Left) {
            // log error ...
        }
        
        complete(result)
    })
}
```