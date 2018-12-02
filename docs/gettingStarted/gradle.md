# Gradle

## Groovy DSL

```groovy
repositories {
    maven {
      url 'https://dl.bintray.com/bebauer/maven'
    }
}

dependencies {
    implementation 'de.bebauer:webflux-handler-dsl:0.5.0'
}
```

## Kotlin DSL

```kotlin
repositories {
    maven {
      url = uri("https://dl.bintray.com/bebauer/maven")
    }
}

dependencies {
    implementation("de.bebauer:webflux-handler-dsl:0.5.0")
}
```