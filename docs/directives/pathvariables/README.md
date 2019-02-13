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

## Extending the DSL

The DSL can be easily extended to provide custom converters for path variables by utilizing the `pathVariable` String extension function.

```kotlin
fun String.myVar() = this.pathVariable { stringValue -> MyObject(stringValue) }
```

This creates a `PathVariable<MyObject>` instance with the specified converter function, which transforms a `String` into `MyObject`.

