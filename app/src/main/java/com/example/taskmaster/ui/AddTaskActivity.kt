package com.example.taskmaster.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmaster.data.Task
import com.example.taskmaster.data.TaskType
import com.example.taskmaster.databinding.ActivityAddTaskBinding
import com.example.taskmaster.viewmodel.TaskViewModel
import com.example.taskmaster.utils.TaskScheduler
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var taskType: TaskType
    private var selectedEndDate: Long? = null
    private val reminderTimes = mutableListOf<String>()
    private lateinit var reminderTimesAdapter: ReminderTimesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Récupérer le type de tâche
        taskType = TaskType.valueOf(
            intent.getStringExtra("TASK_TYPE") ?: TaskType.DAILY.name
        )

        title = if (taskType == TaskType.DAILY) {
            "Nouvelle tâche journalière"
        } else {
            "Nouvelle tâche occasionnelle"
        }

        setupReminderTimesRecyclerView()
        setupListeners()
    }

    private fun setupReminderTimesRecyclerView() {
        reminderTimesAdapter = ReminderTimesAdapter(
            reminderTimes = reminderTimes,
            onDeleteClick = { position ->
                reminderTimes.removeAt(position)
                reminderTimesAdapter.notifyItemRemoved(position)
            }
        )

        binding.rvReminderTimes.apply {
            adapter = reminderTimesAdapter
            layoutManager = LinearLayoutManager(this@AddTaskActivity)
        }
    }

    private fun setupListeners() {
        // Checkbox pour activer/désactiver la date de fin
        binding.cbHasEndDate.setOnCheckedChangeListener { _, isChecked ->
            binding.btnSelectEndDate.isEnabled = isChecked
            if (!isChecked) {
                selectedEndDate = null
                binding.tvSelectedEndDate.visibility = View.GONE
            }
        }

        // Bouton pour sélectionner la date de fin
        binding.btnSelectEndDate.setOnClickListener {
            showDatePicker()
        }

        // Bouton pour ajouter un rappel
        binding.btnAddReminderTime.setOnClickListener {
            showTimePicker()
        }

        // Bouton pour enregistrer la tâche
        binding.btnSaveTask.setOnClickListener {
            saveTask()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedEndDate = calendar.timeInMillis

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.tvSelectedEndDate.text = "Date de fin : ${dateFormat.format(Date(selectedEndDate!!))}"
                binding.tvSelectedEndDate.visibility = View.VISIBLE
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()

        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val timeString = String.format("%02d:%02d", hourOfDay, minute)

                if (!reminderTimes.contains(timeString)) {
                    reminderTimes.add(timeString)
                    reminderTimes.sort()
                    reminderTimesAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Cette heure existe déjà", Toast.LENGTH_SHORT).show()
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // Format 24h
        ).show()
    }

    private fun saveTask() {
        val title = binding.etTaskTitle.text.toString().trim()
        val description = binding.etTaskDescription.text.toString().trim()

        // Validation
        if (title.isEmpty()) {
            binding.etTaskTitle.error = "Le titre est obligatoire"
            return
        }

        // Créer la tâche
        val task = Task(
            title = title,
            description = description.ifEmpty { null },
            taskType = taskType,
            endDate = selectedEndDate,
            reminderTimes = reminderTimes.toList()
        )

        // Enregistrer dans la base de données
        viewModel.insertTask(task) { taskId ->
            runOnUiThread {
                // Planifier les rappels
                val scheduler = TaskScheduler(this)
                val taskWithId = task.copy(id = taskId)
                scheduler.scheduleTaskReminders(taskWithId)

                Toast.makeText(
                    this,
                    "Tâche créée avec succès !",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

}
