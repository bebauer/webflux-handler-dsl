# Read Me

## Webflux Handler DSL

[![Build Status](https://travis-ci.org/bebauer/webflux-handler-dsl.svg?branch=master)](https://travis-ci.org/bebauer/gcloud-scala) [![Download](https://api.bintray.com/packages/bebauer/maven/webflux-handler-dsl/images/download.svg) ](https://bintray.com/bebauer/maven/webflux-handler-dsl/_latestVersion)

This library provides a Kotlin DSL for building Spring Webflux handlers.

## Getting Started

### Gradle

#### Repository

```text
Groovy:

repositories {
    maven {
      url 'https://dl.bintray.com/bebauer/maven'
    }
}

Kotlin:

repositories {
    maven {
      url = uri("https://dl.bintray.com/bebauer/maven")
    }
}
```

#### Dependency

```text
Groovy:

implementation 'de.bebauer:webflux-handler-dsl:0.6.0'

Kotlin:

implementation("de.bebauer:webflux-handler-dsl:0.6.0")
```

### Maven

#### Repository

```xml
<repository>
  <id>bintray-bebauer</id>
  <url>https://dl.bintray.com/bebauer/maven</url>
</repository>
```

#### Dependency

```xml
<dependency>
  <groupId>de.bebauer</groupId>
  <artifactId>webflux-handler-dsl</artifactId>
  <version>0.6.0</version>
</dependency>
```

## Documentation

https://bebauer.gitbook.io/webflux-handler-dsl

## License

Licensed under the Apache License, Version 2.0: [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

