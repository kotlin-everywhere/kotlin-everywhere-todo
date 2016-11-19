package com.example.todo

import java.util.*

var todos = arrayOf<TodoRemote.Todo>()

val remote = TodoRemote().apply {
    add { TodoRemote.Todo(UUID.randomUUID(), it.message).apply { todos += this } }
    list { todos }
    edit { param ->
        todos = todos
                .map { if (it.id != param.id) it else TodoRemote.Todo(id = it.id, message = param.message) }
                .toTypedArray()
        todos.find { it.id == param.id }!!
    }
    delete { id ->
        todos = todos.filter { it.id != id }.toTypedArray()
    }
}

fun main(args: Array<String>) {
    remote.runServer()
}