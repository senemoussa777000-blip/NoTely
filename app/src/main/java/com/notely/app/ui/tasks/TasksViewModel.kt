package com.notely.app.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notely.app.data.local.entity.Task
import com.notely.app.data.repository.TaskRepository
import com.notely.app.domain.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter { TOUTES, AUJOURDUI, A_VENIR, TERMINEES }

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val total: Int = 0,
    val done: Int = 0
) {
    val progress: Float get() = if (total == 0) 0f else done.toFloat() / total
}

class TasksViewModel(private val repository: TaskRepository) : ViewModel() {

    val filter = MutableStateFlow(TaskFilter.TOUTES)

    val uiState: StateFlow<TasksUiState> = combine(
        repository.getAllTasks(),
        filter
    ) { all, f ->
        val filtered = when (f) {
            TaskFilter.TOUTES -> all
            TaskFilter.AUJOURDUI -> all.filter {
                it.dueDate != null && DateUtils.isToday(it.dueDate) && !it.done
            }
            TaskFilter.A_VENIR -> all.filter {
                it.dueDate != null && it.dueDate > DateUtils.endOfDay(System.currentTimeMillis()) && !it.done
            }
            TaskFilter.TERMINEES -> all.filter { it.done }
        }
        TasksUiState(
            tasks = filtered,
            total = all.size,
            done = all.count { it.done }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TasksUiState())

    fun setFilter(value: TaskFilter) { filter.value = value }

    fun saveTask(task: Task) = viewModelScope.launch { repository.save(task) }

    fun toggleDone(task: Task) = viewModelScope.launch {
        repository.setDone(task, !task.done)
    }

    fun delete(task: Task) = viewModelScope.launch { repository.delete(task) }
}
