# Completions

Completions create complete operations. The handler DSL has to return with a `CompleteOperation`.

```kotlin
// ok
handler {
    if (true) {
        complete("1.")
    } else {
        complete("2.")
    }
}

// the following will ignore the first completion and return the second one.
handler {
    complete("1.")
    complete("2.")
}
```

There are different types of complete operations. All those extending `ChainableCompleteOperation` can
also fall back to other operations with the `or` operator.

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

## Directives

* [complete](complete.md)
* [failWith](failwith.md)
