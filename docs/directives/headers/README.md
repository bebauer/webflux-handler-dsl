# Headers

## Directives

* [headerValue](headervalue.md)

## String Extensions

The arguments for the directives expect a `HeaderName` instance. 
Those instances can and should be created through String extensions. 
This way the header value will be converted into the correct type.

### Provided Extension Methods / Properties

* header\(converter\)
* rawHeader
* stringHeader

All but `rawHeader` extract all header values as a list. 
But with the `single` property only the first value with be extracted.
Also it is possible to define a header as optional with the `optional` property.
Then the extracted value will be of type `Optional<T>`.

### Example

```kotlin
"myHeader".header { it.toInt() } // will extract a List<Int>
```

### Single Value Extraction

```kotlin
"myHeader".stringHeader.single // will extract a String
```

### Optional Header

```kotlin
"myHeader".stringHeader.optional // will extract a Option<List<String>>
```

### Single Optional Header

```kotlin
"myHeader".stringHeader.single.optional // will extract a Option<String>
```

### Optional Header with Default Value

```kotlin
"myHeader".stringHeader.optional("abc") // will extract a List<String>
```

### Single Optional Header with Default Value

```kotlin
"myHeader".stringHeader.single.optional("abc") // will extract a String
```

### Nullable Header

```kotlin
"myHeader".stringHeader.nullable // will extract a List<String>?
```

### Single Nullable Header

```kotlin
"myHeader".stringHeader.single.nullable // will extract a String?
```