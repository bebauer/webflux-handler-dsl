# Hello World

This is a quick example of defining a route and using the handler DSL. For more details see the 
[directives](../directives/README.md).

```kotlin
import de.bebauer.webflux.handler.dsl.*

@Configuration
class Config {

    @Bean
    fun routes() = router {
        GET("/", handler {
            complete("Hello World.")
        })
    }
}
```