package de.bebauer.webflux.handler.dsl.example.repository

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface ToDoRepository : ReactiveMongoRepository<ToDo, String> {
    fun findByDone(done: Boolean): Flux<ToDo>
}