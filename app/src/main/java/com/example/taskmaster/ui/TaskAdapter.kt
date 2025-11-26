package com.example.taskmaster.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaster.data.Task
import com.example.taskmaster.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onTaskChecked: (Task, Boolean) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                tvTaskTitle.text = task.title
                tvTaskDescription.text = task.description ?: ""
                checkboxTask.isChecked = task.isCompleted

                if (task.endDate != null) {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    tvEndDate.text = "Fin : ${dateFormat.format(Date(task.endDate))}"
                } else {
                    tvEndDate.text = ""
                }

                if (task.reminderTimes.isNotEmpty()) {
                    tvReminderTimes.text = "🔔 ${task.reminderTimes.joinToString(", ")}"
                } else {
                    tvReminderTimes.text = "Aucun rappel"
                }

                checkboxTask.setOnCheckedChangeListener { _, isChecked ->
                    onTaskChecked(task, isChecked)
                }

                root.setOnClickListener {
                    onTaskClick(task)
                }
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}
