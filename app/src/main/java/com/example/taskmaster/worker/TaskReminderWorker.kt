package com.example.taskmaster.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.taskmaster.data.TaskDatabase
import com.example.taskmaster.utils.NotificationHelper
import java.util.*

class TaskReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong("TASK_ID", -1)

        if (taskId == -1L) {
            return Result.failure()
        }

        val taskDao = TaskDatabase.getDatabase(applicationContext).taskDao()
        val task = taskDao.getTaskById(taskId)

        task?.let {
            // Vérifier si la tâche n'est pas déjà complétée aujourd'hui
            if (!isCompletedToday(it.isCompleted, it.lastCompletedDate)) {
                val notificationHelper = NotificationHelper(applicationContext)
                notificationHelper.showTaskReminder(it)
            }
        }

        return Result.success()
    }

    private fun isCompletedToday(isCompleted: Boolean, lastCompletedDate: Long?): Boolean {
        if (!isCompleted || lastCompletedDate == null) {
            return false
        }

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        return lastCompletedDate >= today
    }
}
