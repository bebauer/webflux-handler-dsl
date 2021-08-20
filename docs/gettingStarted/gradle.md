# Gradle

## Groovy DSL

```groovy
repositories {
    maven {
        url "https://maven.pkg.github.com/bebauer/webflux-handler-dsl"
        credentials {
            username = "github username"
            password = "github personal access token with read:packages scope"
        }
    }
}

dependencies {
    implementation 'de.bebauer:webflux-handler-dsl:1.2.1'
}
```

## Kotlin DSL

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/bebauer/webflux-handler-dsl")
        credentials {
            username = "github username"
            password = "github personal access token with read:packages scope"
        }
    }
}

dependencies {
    implementation("de.bebauer:webflux-handler-dsl:1.2.1")
}
```