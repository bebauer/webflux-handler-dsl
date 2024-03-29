## Webflux Handler DSL

[![Java CI with Gradle](https://github.com/bebauer/webflux-handler-dsl/actions/workflows/ci.yaml/badge.svg)](https://github.com/bebauer/webflux-handler-dsl/actions/workflows/ci.yaml)

This library provides a Kotlin DSL for building Spring Webflux handlers. It starts
where the routing DSL from Spring ends.

[Release Notes](docs/releaseNotes.md)

### Example

```kotlin
import de.bebauer.webflux.handler.dsl.*

@Configuration
class Config(private val repository: ItemRepository) {

    private val getItem = handler {
        pathVariable("id".stringVar) { id ->
            complete(repository.findById(id)) or notFound()
        }
    }

    @Bean
    fun routes() = router {
        GET("/item/{id}", getItem)
    }
}
```

## Getting Started

### Gradle

#### Repository

```text
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/bebauer/webflux-handler-dsl")
        credentials {
            username = "github username"
            password = "github personal access token with read:packages scope"
        }
    }
}
```

#### Dependency

```text
Groovy:

implementation 'de.bebauer:webflux-handler-dsl:1.2.1'

Kotlin:

implementation("de.bebauer:webflux-handler-dsl:1.2.1")
```

### Maven

#### Repository

```xml
<repository>
  <snapshots>
    <enabled>false</enabled>
  </snapshots>
  <id>bebauer</id>
  <name>bebauer-jfrog</name>
  <url>https://bebauer.jfrog.io/artifactory/maven-releases</url>
</repository>
```

#### Dependency

```xml
<dependency>
  <groupId>de.bebauer</groupId>
  <artifactId>webflux-handler-dsl</artifactId>
  <version>1.2.1</version>
</dependency>
```

## Documentation

https://bebauer.gitbook.io/webflux-handler-dsl

## License

Licensed under the Apache License, Version 2.0: [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

