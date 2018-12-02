# Path Variables

## Directives

* [pathVariable](pathvariable.md)
* [pathVariables](pathvariables.md)

## String Extensions

The arguments for the directives expect a PathVariable instance. Those instances can and should be created through String extensions. This way the path variable will be converted into the correct type.

### Provided Extension Methods

* stringVar\(\)
* booleanVar\(\)
* byteVar\(\)
* shortVar\(\)
* intVar\(\)
* longVar\(\)
* bigIntegerVar\(\)
* floatVar\(\)
* doubleVar\(\)
* bigDecimalVar\(\)
* uByteVar\(\)
* uShortVar\(\)
* uIntVar\(\)
* uLongVar\(\)

### Example

```kotlin
"id".longVar() // creates PathVariable<Long> instance for the "id" variable
```

## Extending the DSL

The DSL can be easily extended to provide custom converters for path variables by utilizing the `pathVariable` String extension function.

```kotlin
fun String.myVar() = this.pathVariable { stringValue -> MyObject(stringValue) }
```

This creates a `PathVariable<MyObject>` instance with the specified converter function, which transforms a `String` into `MyObject`.

