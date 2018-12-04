# Cookies

## Directives

* [cookie](cookie.md)

## String Extensions

The arguments for the directives expect a `CookieName` instance. 
Those instances can and should be created through String extensions. 
This way the cookie value will be converted into the correct type.

### Provided Extension Methods

* cookieName\(converter\)
* stringCookie\(\)

All these extract all cookies with the name as a list. 
But with the `single()` method only the first value with be extracted.
Also it is possible to define a cookie as optional with the `optional()` method.
Then the extracted value will be of type `Optional<T>`.

### Example

```kotlin
"myCookie".cookieName { it.toInt() } // will extract a List<Int>
```

### Single Value Extraction

```kotlin
"myCookie".stringCookie().single() // will extract a String
```

### Optional Header

```kotlin
"myCookie".stringCookie().optional() // will extract a Option<List<String>>
```

### Single Optional Header

```kotlin
"myCookie".stringCookie().single().optional() // will extract a Option<String>
```

### Optional Header with Default Value

```kotlin
"myCookie".stringCookie().optional("abc") // will extract a List<String>
```

### Single Optional Header with Default Value

```kotlin
"myCookie".stringCookie().single().optional("abc") // will extract a String
```