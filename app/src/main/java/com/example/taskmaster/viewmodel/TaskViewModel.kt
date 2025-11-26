package com.example.taskmaster.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.taskmaster.data.Task
import com.example.taskmaster.data.TaskDatabase
import com.example.taskmaster.data.TaskRepository
import com.example.taskmaster.data.TaskType
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    val dailyTasks: LiveData<List<Task>>
    val occasionalTasks: LiveData<List<Task>>

    init {
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        dailyTasks = repository.getTasksByType(TaskType.DAILY)
        occasionalTasks = repository.getTasksByType(TaskType.OCCASIONAL)
    }

    fun insertTask(task: Task, onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val taskId = repository.insertTask(task)
            onSuccess(taskId)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTaskCompletion(taskId, !isCompleted)
        }
    }

    fun resetDailyTasks() {
        viewModelScope.launch {
            repository.resetDailyTasks()
        }
    }
}
