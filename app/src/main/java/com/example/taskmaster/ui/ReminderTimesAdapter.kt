package com.example.taskmaster.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaster.databinding.ItemReminderTimeBinding

class ReminderTimesAdapter(
    private val reminderTimes: List<String>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<ReminderTimesAdapter.ReminderTimeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderTimeViewHolder {
        val binding = ItemReminderTimeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReminderTimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderTimeViewHolder, position: Int) {
        holder.bind(reminderTimes[position])
    }

    override fun getItemCount(): Int = reminderTimes.size

    inner class ReminderTimeViewHolder(
        private val binding: ItemReminderTimeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(time: String) {
            binding.tvReminderTime.text = time

            binding.btnDeleteReminderTime.setOnClickListener {
                onDeleteClick(adapterPosition)
            }
        }
    }
}
