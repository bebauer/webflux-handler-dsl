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

## Directives

* [complete](complete.md)
* [failWith](failwith.md)
