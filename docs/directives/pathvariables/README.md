# Path Variables

## Directives

* [pathVariable](pathvariable.md)
* [pathVariables](pathvariables.md)

## String Extensions

The arguments for the directives expect a `PathVariable` instance. 
Those instances can and should be created through String extensions. 
This way the path variable will be converted into the correct type.

### Provided Extension Methods / Properties

* pathVariable\(converter\)
* stringVar
* booleanVar
* byteVar
* shortVar
* intVar
* longVar
* bigIntegerVar
* floatVar
* doubleVar
* bigDecimalVar
* uByteVar
* uShortVar
* uIntVar
* uLongVar
* enumVar<T>\(\)

### Example

```kotlin
"id".longVar // creates PathVariable<Long> instance for the "id" variable
```

### Mapping Values

PathVariable values can be mapped with the `map` function.

```kotlin
"id".stringVar.map { it.toInt() }
```

There are also convenient functions for string variables.

```kotlin
"id".stringVar.toUpperCase

"id".stringVar.toLowerCase

"id".stringVar.toEnum<SomeEnum>()

"id".stringVar.toUpperCase.toEnum<SomeEnum>()
```

### Optional Variables

Path variables can also be defined as optional. For example if the same logic should apply to different routes. 
To do this use the `optional` extension method / property.

```kotlin
"query".stringVar.optional
```

This will extract an `Option<T>`.

A default value can also be specified. In this case `T` will be extracted an not `Option<T>`.

```kotlin
"query".stringVar.optional("default")
```

### Nullable Variables

Path variables can also be defined as nullable, if using Kotlin nullable types is preferred to Arrow's Option. 
To do this use the `nullable` extension property.

```kotlin
"query".stringVar.nullable
```

## Extending the DSL

The DSL can be easily extended to provide custom converters for path variables by utilizing the `pathVariable` String extension function.

```kotlin
fun String.myVar() = this.pathVariable { stringValue -> MyObject(stringValue) }
```

This creates a `PathVariable<MyObject>` instance with the specified converter function, which transforms a `String` into `MyObject`.

