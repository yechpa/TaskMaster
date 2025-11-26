package com.example.taskmaster.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

enum class TaskType {
    DAILY,
    OCCASIONAL
}

@Entity(tableName = "tasks")
@TypeConverters(Converters::class)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "task_type")
    val taskType: TaskType,

    @ColumnInfo(name = "end_date")
    val endDate: Long? = null,

    @ColumnInfo(name = "reminder_times")
    val reminderTimes: List<String> = emptyList(),

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "last_completed_date")
    val lastCompletedDate: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
