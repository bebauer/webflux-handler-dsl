# Futures

## Directives

* [onComplete](onComplete.md)
* [onSuccess](onSuccess.md)

## Important

The future directives use features from Java 9 and 11. Only the directives without a timeout will work with Java 8.
And the directives which accept a `java.time.Duration`, either directly or as a `Option`, 
will only work with Java 11+. 