package com.example.taskmaster.data

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {

    fun getTasksByType(taskType: TaskType): LiveData<List<Task>> {
        return taskDao.getTasksByType(taskType)
    }

    fun getAllTasks(): LiveData<List<Task>> {
        return taskDao.getAllTasks()
    }

    suspend fun getTaskById(taskId: Long): Task? {
        return taskDao.getTaskById(taskId)
    }

    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        val completedDate = if (isCompleted) System.currentTimeMillis() else null
        taskDao.updateTaskCompletion(taskId, isCompleted, completedDate)
    }

    suspend fun resetDailyTasks() {
        taskDao.resetDailyTasks()
    }
}
