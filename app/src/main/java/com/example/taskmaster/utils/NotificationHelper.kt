package com.example.taskmaster.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.taskmaster.R
import com.example.taskmaster.data.Task
import com.example.taskmaster.ui.MainActivity

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "task_reminder_channel"
        const val CHANNEL_NAME = "Rappels de Tâches"
        const val CHANNEL_DESCRIPTION = "Notifications pour les rappels de tâches"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showTaskReminder(task: Task) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("TASK_ID", task.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("📋 Rappel de tâche")
            .setContentText(task.title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${task.title}\n${task.description ?: ""}")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                android.R.drawable.ic_menu_save,
                "Marquer comme fait",
                createMarkAsDoneIntent(task.id)
            )
            .build()

        try {
            NotificationManagerCompat.from(context).notify(task.id.toInt(), notification)
        } catch (e: SecurityException) {
            // Permission de notification non accordée
            e.printStackTrace()
        }
    }

    private fun createMarkAsDoneIntent(taskId: Long): PendingIntent {
        val intent = Intent(context, TaskActionReceiver::class.java).apply {
            action = "MARK_AS_DONE"
            putExtra("TASK_ID", taskId)
        }

        return PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
