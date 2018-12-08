# execute

## Signature

```kotlin
fun execute(init: HandlerDsl.() -> Unit)
    : Mono<ServerResponse>
```

## Description

`execute` runs a handler DSL and return it's result, which is the `Mono<ServerRespone>` from the nested block.

## Example

```kotlin
router {
    GET("/", handler {
        val response = execute {
            complete("ok")
        }
        
        complete(response.onErrorResume {
            println(it) // log the error
            
            Mono.error(it)
        })
    })
}
```