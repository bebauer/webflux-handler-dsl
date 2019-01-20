# Query Parameters

## Directives

* [parameter](parameter.md)
* [parameters](parameters.md)

## String Extensions

The arguments for the directives expect a QueryParameter instance. 
Those instances can and should be created through String extensions.

### Provided Extension Methods / Properties

* queryParam\(converter\)
* stringParam
* booleanParam
* byteParam
* shortParam
* intParam
* longParam
* bigIntegerParam
* floatParam
* doubleParam
* bigDecimalParam
* uByteParam
* uShortParam
* uIntParam
* uLongParam
* csvParam
* csvParam\(converter\)

### Example

```kotlin
"query".stringParam // creates QueryParameter<String, String> instance for the "query" parameter
```

### Repeated Parameters

Query parameters can appear more than once in an url. By default only the first parameter will be extracted.
To get all specified parameter use the `repeated` extension property.

```kotlin
"query".stringParam.repeated
```

This will extract a `List<T>`. 

### Optional Parameters

Query parameters can also be defined as optional. To do this use the `optional` extension method / property.

```kotlin
"query".stringParam.optional
```

This will extract an `Option<T>`.

A default value can also be specified. In this case `T` will be extracted an not `Option<T>`.

```kotlin
"query".stringParam.optional("default")
```

### Repeated Optional Parameters

`optional` can also be combined with `repeated`, but `repeated` has to be called before.

```kotlin
"query".stringParam.repeated.optional
```

or

```kotlin
"query".stringParam.repeated.optional(listOf("default"))
```

### Nullable Parameters

Query parameters can also be defined as nullable, if using Kotlin nullable types if preferred to Arrow's Option. 
To do this use the `nullable` extension property.

```kotlin
"query".stringParam.nullable
```

This will extract an `T?`.

### Repeated Nullable Parameters

`nullable` can also be combined with `repeated`, but `repeated` has to be called before.

```kotlin
"query".stringParam.repeated.nullable
```

### CSV Parameters

CSV parameters `(e.g. ?param=a,b,c)` can be extracted with the `csvParam` extension method / property. 
The extracted parameter will be of the type `List<String>`.

## Extending the DSL

The DSL can be easily extended to provide custom converters for query parameters by utilizing the `queryParam` or
`csvParam` String extensions, which accept a custom converter as parameter.

```kotlin
fun String.myParam() = this.queryParam { stringValue -> MyObject(stringValue) }
```

This creates a `QueryParameter<MyObject, MyObject>` instance with the specified converter function, 
which transforms a `String` into `MyObject`.
