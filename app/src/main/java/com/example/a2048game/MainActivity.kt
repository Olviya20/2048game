package com.example.a2048game

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.cardview.widget.CardView
import com.example.a2048game.databinding.ActivityMainBinding
import com.example.a2048game.history.AppDatabase
import com.example.a2048game.history.GameSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentSizeGrid = 4
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)


        CoroutineScope(Dispatchers.Main).launch {
            val sessions = withContext(Dispatchers.IO) {
                db.gameSessionDao().getAllSessions()
            }

            for (session in sessions) {
                Log.d("MyLog", "GameSession: $session")
            }
        }


        binding.et2.alpha = 0.3f
        binding.et3.alpha = 0.3f
        binding.et4.alpha = 0.3f

        binding.et1.setOnClickListener {
            currentSizeGrid = 4
            binding.et1.alpha = 1f
            changeAlpha(binding.et1)

        }

        binding.et2.setOnClickListener {
            currentSizeGrid = 5
            binding.et2.alpha = 1f
            changeAlpha(binding.et2)

        }

        binding.et3.setOnClickListener {
            currentSizeGrid = 6
            binding.et3.alpha = 1f
            changeAlpha(binding.et3)

        }

        binding.et4.setOnClickListener {
            currentSizeGrid = 8
            binding.et4.alpha = 1f
            changeAlpha(binding.et4)

        }


        binding.cvPlay.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("gridSize", currentSizeGrid)
            startActivity(intent)
        }

        binding.cvHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    private fun changeAlpha(et: CardView) {
        val typeGrids = listOf(
            binding.et1,
            binding.et2,
            binding.et3,
            binding.et4,
        )
        for (i in typeGrids) {
            if (i !== et) {
                i.alpha = 0.3f
            }
        }
    }
}