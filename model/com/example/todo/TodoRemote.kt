package com.example.todo

import com.github.kotlin_everywhere.rpc.Remote
import com.github.kotlin_everywhere.rpc.get
import com.github.kotlin_everywhere.rpc.post
import com.github.kotlin_everywhere.rpc.put
import com.github.kotlin_everywhere.rpc.delete
import java.util.*

class TodoRemote : Remote() {
    val add = post<AddTodo, Todo>()
    val edit = put<EditTodo, Todo>()
    val delete = delete<UUID, Unit>()
    val list = get<Unit, Array<Todo>>()

    data class AddTodo(val message: String)
    data class EditTodo(val id: UUID, val message: String)
    data class Todo(val id: UUID, val message: String)
}
