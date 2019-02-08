package de.bebauer.webflux.handler.dsl.example.repository

import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface ToDoRepository : ReactiveMongoRepository<ToDo, String>