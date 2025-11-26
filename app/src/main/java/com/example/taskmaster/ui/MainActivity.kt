package com.example.taskmaster.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmaster.data.TaskType
import com.example.taskmaster.databinding.ActivityMainBinding
import com.example.taskmaster.viewmodel.TaskViewModel
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter
    private var currentTaskType = TaskType.DAILY

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission accordée
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupTabs()
        setupFab()
        observeTasks()
        requestNotificationPermission()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskClick = { task ->
                // TODO: Ouvrir écran de modification
            },
            onTaskChecked = { task, isChecked ->
                viewModel.toggleTaskCompletion(task.id, task.isCompleted)
            }
        )

        binding.recyclerView.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTaskType = if (tab?.position == 0) {
                    TaskType.DAILY
                } else {
                    TaskType.OCCASIONAL
                }
                observeTasks()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupFab() {
        binding.fabAddTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putExtra("TASK_TYPE", currentTaskType.name)
            startActivity(intent)
        }
    }

    private fun observeTasks() {
        val tasksLiveData = if (currentTaskType == TaskType.DAILY) {
            viewModel.dailyTasks
        } else {
            viewModel.occasionalTasks
        }

        tasksLiveData.observe(this) { tasks ->
            taskAdapter.submitList(tasks)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
