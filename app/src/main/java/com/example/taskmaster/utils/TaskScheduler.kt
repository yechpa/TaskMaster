package com.example.taskmaster.utils

import android.content.Context
import androidx.work.*
import com.example.taskmaster.data.Task
import com.example.taskmaster.data.TaskType
import com.example.taskmaster.worker.TaskReminderWorker
import java.util.*
import java.util.concurrent.TimeUnit

class TaskScheduler(private val context: Context) {

    fun scheduleTaskReminders(task: Task) {
        // Annuler les anciens rappels pour cette tâche
        cancelTaskReminders(task.id)

        // Planifier les nouveaux rappels
        task.reminderTimes.forEach { time ->
            scheduleReminderForTime(task, time)
        }
    }

    private fun scheduleReminderForTime(task: Task, time: String) {
        val (hour, minute) = time.split(":").map { it.toInt() }
        val delay = calculateDelayUntilTime(hour, minute)

        val data = workDataOf("TASK_ID" to task.id)

        if (task.taskType == TaskType.DAILY) {
            // Pour les tâches journalières, utiliser PeriodicWorkRequest
            val workRequest = PeriodicWorkRequestBuilder<TaskReminderWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("task_${task.id}")
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "task_${task.id}_$time",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        } else {
            // Pour les tâches occasionnelles, utiliser OneTimeWorkRequest
            val workRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("task_${task.id}")
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "task_${task.id}_$time",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    private fun calculateDelayUntilTime(hour: Int, minute: Int): Long {
        val currentCalendar = Calendar.getInstance()
        val targetCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Si l'heure est déjà passée aujourd'hui, planifier pour demain
        if (targetCalendar.timeInMillis <= currentCalendar.timeInMillis) {
            targetCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return targetCalendar.timeInMillis - currentCalendar.timeInMillis
    }

    fun cancelTaskReminders(taskId: Long) {
        WorkManager.getInstance(context).cancelAllWorkByTag("task_$taskId")
    }
}
