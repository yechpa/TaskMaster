package com.example.taskmaster.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE task_type = :taskType ORDER BY created_at DESC")
    fun getTasksByType(taskType: TaskType): LiveData<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY created_at DESC")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("UPDATE tasks SET is_completed = :isCompleted, last_completed_date = :completedDate WHERE id = :taskId")
    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean, completedDate: Long?)

    @Query("UPDATE tasks SET is_completed = 0, last_completed_date = NULL WHERE task_type = 'DAILY'")
    suspend fun resetDailyTasks()
}
