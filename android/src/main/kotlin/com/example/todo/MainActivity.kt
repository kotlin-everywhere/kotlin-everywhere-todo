package com.example.todo

import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout
import com.github.kotlin_everywhere.rpc.init
import trikita.anvil.Anvil
import trikita.anvil.DSL.*
import trikita.anvil.RenderableAdapter
import trikita.anvil.RenderableView

class MainActivity : Activity() {
    private var state = State()
        set(value) {
            if (field.todos != value.todos) {
                todos.removeAll { true }
                todos.addAll(value.todos)
            }
            field = value
            Anvil.render()
        }
    private val todos = arrayListOf<TodoRemote.Todo>()
    private val remote = TodoRemote().init("http://192.168.0.8:8080")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val todoAdapter = RenderableAdapter.withItems(todos) { pos, value ->
            linearLayout {
                size(FILL, WRAP)

                textView {
                    size(WRAP, WRAP)
                    padding(dip(5))
                    text(value.message)
                }

                button {
                    size(WRAP, WRAP)
                    text("수정")

                    onClick {
                        state = state.copy(edit = TodoRemote.EditTodo(value.id, value.message))
                    }
                }

                button {
                    size(WRAP, WRAP)
                    text("삭제")

                    onClick {
                        remote.delete(value.id) {
                            state = state.copy(todos = state.todos.filter { it.id != value.id })
                        }
                    }
                }
            }
        }

        setContentView(object : RenderableView(this) {
            override fun view() {
                todoAdapter.notifyDataSetChanged()

                linearLayout {
                    size(MATCH, MATCH)
                    padding(dip(8))
                    orientation(LinearLayout.VERTICAL)

                    val edit = state.edit
                    if (edit == null) {
                        editText {
                            size(FILL, WRAP)

                            text(state.message)
                            onTextChanged { state = state.copy(message = it.toString()) }
                        }

                        button {
                            size(FILL, WRAP)
                            text("추가")

                            onClick {
                                val message = state.message.trim()
                                if (message.isNotEmpty()) {
                                    state = state.copy(message = "")
                                    remote.add(TodoRemote.AddTodo(message)) {
                                        state = state.copy(todos = state.todos + it)
                                    }
                                }
                            }

                        }
                    } else {
                        editText {
                            size(FILL, WRAP)

                            text(edit.message)
                            onTextChanged { state = state.copy(edit = edit.copy(message = it.toString())) }
                        }

                        button {
                            size(FILL, WRAP)
                            text("수정")

                            onClick {
                                val message = edit.message.trim()
                                if (message.isNotEmpty()) {
                                    state = state.copy(edit = null)
                                    remote.edit(edit) { todo ->
                                        state = state.copy(todos = state.todos.map { if (it.id == todo.id) todo else it })
                                    }
                                }
                            }

                        }
                    }

                    listView {
                        size(FILL, WRAP)
                        itemsCanFocus(true)
                        adapter(todoAdapter)
                    }
                }
            }
        })

        remote.list(Unit) {
            state = state.copy(todos = it.toList())
        }
    }

    data class State(
            val message: String = "", val todos: List<TodoRemote.Todo> = listOf(),
            val edit: TodoRemote.EditTodo? = null
    )
}
