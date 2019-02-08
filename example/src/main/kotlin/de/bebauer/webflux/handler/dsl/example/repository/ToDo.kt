package de.bebauer.webflux.handler.dsl.example.repository

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class ToDo(
    @Id val id: String?,
    val title: String,
    val done: Boolean? = false
)