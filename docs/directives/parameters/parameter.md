# parameter

## Signature

```kotlin
fun <T, U> HandlerDsl.parameter(
    parameter: QueryParameter<T, U>, 
    init: HandlerDsl.(T) -> Unit)
```

## Description

Extracts a query parameter from the `ServerRequest`. See [parameters](parameters.md) if more than one 
parameter should be extracted. See [Query Parameters](README.md) for a description of how to build `QueryParameter`
instances.

## Examples

```kotlin
// required parameter
router {
    GET("/", handler {
        parameter("query".stringParam()) { query ->
            complete(repo.findByQuery(query))
        }
    })
}

// optional parameter
router {
    GET("/", handler {
        parameter("query".stringParam().optional()) { query ->
            query.map {
                complete(repo.findByQuery(it))
            }.orElse {
                complete("Some fallback.")   
            }
        }
    })
}

// optional parameter with default
router {
    GET("/", handler {
        parameter("query".stringParam().optional("*")) { query ->
            complete(repo.findByQuery(query))
        }
    })
}

// repeated parameter
router {
    GET("/", handler {
        parameter("query".stringParam().repeated()) { queries ->
            complete(Flux.fromIterable(queries).flatMap { 
                repo.findByQuery(it) 
            })
        }
    })
}

// repeated optional parameter
router {
    GET("/", handler {
        parameter("query".stringParam().repeated().optional()) { queries ->
            queries.map {
                complete(Flux.fromIterable(it).flatMap { 
                    repo.findByQuery(it) 
                })
            }.orElse {
                complete("Some fallback.") 
            }
        }
    })
}

// repeated optional parameter with default
router {
    GET("/", handler {
        parameter("query".stringParam()
                         .repeated()
                         .optional(listOf("1", "2"))
        ) { queries ->
            complete(Flux.fromIterable(queries).flatMap { 
                repo.findByQuery(it) 
            })
        }
    })
}
```
