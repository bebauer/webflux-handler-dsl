# Gradle

## Groovy DSL

```groovy
repositories {
    maven {
        url "https://bebauer.jfrog.io/artifactory/maven-releases"
    }
}

dependencies {
    implementation 'de.bebauer:webflux-handler-dsl:1.1.0'
}
```

## Kotlin DSL

```kotlin
repositories {
    maven {
        url = uri("https://bebauer.jfrog.io/artifactory/maven-releases")
    }
}

dependencies {
    implementation("de.bebauer:webflux-handler-dsl:1.1.0")
}
```