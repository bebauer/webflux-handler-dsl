package de.bebauer.webflux.handler.dsl.example

import arrow.core.getOrElse
import de.bebauer.webflux.handler.dsl.*
import de.bebauer.webflux.handler.dsl.example.repository.ToDo
import de.bebauer.webflux.handler.dsl.example.repository.ToDoRepository
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class ToDoHandler(val repository: ToDoRepository) {

    val getAll = handler {
        parameter("done".booleanParam.optional) { done ->
            done.map {
                ok(repository.findByDone(it))
            }.getOrElse {
                ok(repository.findAll())
            }
        }
    }

    val create = handler {
        extractRequestBodyToMono<ToDo> { body ->
            body.flatMap { repository.save(it) }
                .map {
                    created(it) {
                        header(HttpHeaders.LOCATION, "/todos/${it.id}")
                    }
                }
                .toCompleteOperation()
        }
    }

    val delete = handler {
        pathVariable("id".stringVar) { id ->
            repository.deleteById(id)
                .map { complete(HttpStatus.NO_CONTENT) }
                .toCompleteOperation()
        }
    }

    val get = handler {
        pathVariable("id".stringVar) { id ->
            repository.findById(id)
                .map<CompleteOperation> { ok(it) }
                .defaultIfEmpty(notFound())
                .toCompleteOperation()
        }
    }

    val update = handler {
        pathVariable("id".stringVar) { id ->
            extractRequestBodyToMono<ToDo> { body ->
                body.flatMap { repository.save(it.copy(id = id)) }
                    .map { ok(it) }
                    .toCompleteOperation()
            }
        }
    }
}