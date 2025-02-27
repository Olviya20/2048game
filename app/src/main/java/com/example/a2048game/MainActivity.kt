package com.example.a2048game

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.example.a2048game.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentSizeGrid = 4
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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