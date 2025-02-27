package com.example.a2048game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a2048game.databinding.ActivityHistoryBinding
import com.example.a2048game.history.AppDatabase
import com.example.a2048game.history.GameSessionDao
import com.example.a2048game.history.HistoryAdapter
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var gameSessionDao: GameSessionDao

    private lateinit var binding: ActivityHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.rvHistory
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = HistoryAdapter()
        recyclerView.adapter = adapter

        gameSessionDao = AppDatabase.getInstance(this).gameSessionDao()

        loadHistory()

    }

    private fun loadHistory() {
        lifecycleScope.launch {
            try {
                val sessions = gameSessionDao.getAllSessions()
                adapter.submitList(sessions)
            } catch (e: Exception) {
                Toast.makeText(this@HistoryActivity, "Error loading history: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}