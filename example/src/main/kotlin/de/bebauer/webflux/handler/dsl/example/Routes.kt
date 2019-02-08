package de.bebauer.webflux.handler.dsl.example

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class Routes(val handler: ToDoHandler) {

    @Bean
    fun getRouter() = router {
        "/todos".nest {
            GET("/", handler.getAll)

            GET("/{id}", handler.get)

            POST("/", handler.create)

            DELETE("/{id}", handler.delete)

            PUT("/{id}", handler.update)
        }
    }
}