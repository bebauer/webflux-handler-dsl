# Completions

Completions are terminating directives. These can be called only once in an execution.

```kotlin
// ok
handler {
    if (true) {
        complete("1.")
    } else {
        complete("2.")
    }
}

// the following will fail with an internal server error
handler {
    complete("1.")
    complete("2.")
}
```

In the end complete will end in an `Mono<ServerResponse>`. Therefore the `or` operator can be used on completions.
When the `Mono<ServerResponse>` is empty, the alternative completion will be returned.

```kotlin
handler {
    complete(Mono.empty()) or complete(HttpStatus.NOT_FOUND) 
    // Returns 404 Status
}

handler {
    complete("123") or complete(HttpStatus.NOT_FOUND) 
    // Unnecessary, because it Will always return 200 with body 123.
}
```

Completions can be chained indefinitely and the first non empty `Mono<ServerResponse>` will be returned.

## Directives

* [complete](complete.md)
* [failWith](failwith.md)
