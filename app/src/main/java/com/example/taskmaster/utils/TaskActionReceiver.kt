package com.example.taskmaster.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.taskmaster.data.TaskDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "MARK_AS_DONE") {
            val taskId = intent.getLongExtra("TASK_ID", -1)

            if (taskId != -1L) {
                markTaskAsDone(context, taskId)

                // Fermer la notification
                NotificationManagerCompat.from(context).cancel(taskId.toInt())
            }
        }
    }

    private fun markTaskAsDone(context: Context, taskId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val taskDao = TaskDatabase.getDatabase(context).taskDao()
            taskDao.updateTaskCompletion(taskId, true, System.currentTimeMillis())
        }
    }
}
