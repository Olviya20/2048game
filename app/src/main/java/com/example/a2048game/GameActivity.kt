package com.example.a2048game

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.TextView
import com.example.a2048game.databinding.ActivityGameBinding
import kotlin.random.Random
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import com.example.a2048game.history.AppDatabase
import com.example.a2048game.history.GameSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Stack


class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var gridLayout: GridLayout
    private lateinit var gestureDetector: GestureDetectorCompat
    private var gridSize: Int = 4
    private lateinit var db: AppDatabase


    private var score: Int = 0
    private val previousStates: Stack<Array<Array<Tile>>> = Stack()
    private var grid = Array(gridSize) { Array(gridSize) { Tile() } }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gridSize = intent.getIntExtra("gridSize", 4)
        db = AppDatabase.getInstance(this)

        gridLayout = binding.gridLayout
        gridLayout.columnCount = gridSize
        gridLayout.rowCount = gridSize

        initGridUI()
        initGame()

        gestureDetector = GestureDetectorCompat(this, object : SwipeGestureListener(this) {
            override fun onSwipeRight() {
                saveState()
                moveRight()
            }

            override fun onSwipeLeft() {
                saveState()
                moveLeft()
            }

            override fun onSwipeUp() {
                saveState()
                moveUp()
            }

            override fun onSwipeDown() {
                saveState()
                moveDown()
            }
        })

        binding.cvUndo.alpha = 0.5f

        binding.cvHome.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val session = GameSession(score = score, date = System.currentTimeMillis())
                db.gameSessionDao().insertSession(session)
            }
            if (score > loadHighScore()) {
                saveHighScore(score)
            }
            finish()
        }

        binding.cvUndo.setOnClickListener {
            undoMove()
        }

        binding.cvReboot.setOnClickListener {
            showResetConfirmationDialog()
        }

        binding.tvRecordScore.text = loadHighScore().toString()
        binding.tvScore.text = "0"

    }

    private fun saveHighScore(newHighScore: Int) {
        val sharedPreferences = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        binding.tvRecordScore.text = newHighScore.toString()
        editor.putInt("HighScore", newHighScore)
        editor.apply()
    }

    private fun loadHighScore(): Int {
        val sharedPreferences = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("HighScore", 0)
    }

    private fun addScore(value: Int) {
        score += value
        binding.tvScore.text = score.toString()

        val currScore = binding.tvScore.text.toString()
        val currRecordStore = binding.tvRecordScore.text.toString()

        if (currScore.toInt() > currRecordStore.toInt()) {
            binding.tvRecordScore.text = currScore
        }
    }


    private fun showResetConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Подтверждение")
        builder.setMessage("Вы уверены, что хотите начать заново?")

        val currentScoreText = binding.tvScore.text.toString()
        val currentScore = currentScoreText.toIntOrNull() ?: 0

        builder.setPositiveButton("Да") { dialog, which ->
            initGridUI()
            initGame()


            CoroutineScope(Dispatchers.IO).launch {
                val session = GameSession(score = score, date = System.currentTimeMillis())
                db.gameSessionDao().insertSession(session)
            }

            if (currentScore > loadHighScore()) {
                saveHighScore(binding.tvScore.text.toString().toInt())
                binding.tvRecordScore.text = binding.tvScore.text
                binding.tvScore.text = "0"
                score = 0
            }
        }


        builder.setNegativeButton("Нет") { dialog, which ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun undoMove() {
        if (previousStates.isNotEmpty()) {
            grid = previousStates.pop()
            updateUI()
            if (previousStates.isEmpty()) {
                binding.cvUndo.alpha = 0.5f
            }
        }
    }

    private fun saveState() {
        binding.cvUndo.alpha = 1f
        val currentState = copyGrid()
        previousStates.push(currentState)
    }

    private fun copyGrid(): Array<Array<Tile>> {
        return Array(grid.size) { row ->
            Array(grid[row].size) { col ->
                val oldTile = grid[row][col]
                Tile(oldTile.value)
            }
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private fun initGridUI() {
        gridLayout.removeAllViews()

        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                val tileView = LayoutInflater.from(this)
                    .inflate(R.layout.tile_view, gridLayout, false) as FrameLayout
                val params = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    columnSpec = GridLayout.spec(c, 1f)
                    rowSpec = GridLayout.spec(r, 1f)
                    setMargins(6, 6, 6, 6)
                }
                tileView.layoutParams = params
                gridLayout.addView(tileView)
            }
        }
    }

    private fun initGame() {
        grid = Array(gridSize) { Array(gridSize) { Tile() } }
        addRandomTile()
        addRandomTile()
        updateUI()
    }

    private fun addRandomTile() {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                if (grid[r][c].value == 0) emptyCells.add(Pair(r, c))
            }
        }
        if (emptyCells.isNotEmpty()) {
            val (row, col) = emptyCells.random()
            grid[row][col].value = if (Random.nextDouble() < 0.9) 2 else 4
        }
    }

    private fun updateUI() {
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                val tileIndex = r * gridSize + c
                val tileView = gridLayout.getChildAt(tileIndex) as FrameLayout
                val textView = tileView.findViewById<TextView>(R.id.tv_tile)
                val value = grid[r][c].value

                textView.text = if (value > 0) value.toString() else ""
                tileView.setBackgroundColor(getTileColor(value))
            }
        }
    }

    private fun getTileColor(value: Int): Int {
        return when (value) {
            2 -> ContextCompat.getColor(this, R.color.tile_2)
            4 -> ContextCompat.getColor(this, R.color.tile_4)
            8 -> ContextCompat.getColor(this, R.color.tile_8)
            16 -> ContextCompat.getColor(this, R.color.tile_16)
            32 -> ContextCompat.getColor(this, R.color.tile_32)
            64 -> ContextCompat.getColor(this, R.color.tile_64)
            128 -> ContextCompat.getColor(this, R.color.tile_128)
            256 -> ContextCompat.getColor(this, R.color.tile_256)
            512 -> ContextCompat.getColor(this, R.color.tile_512)
            1024 -> ContextCompat.getColor(this, R.color.tile_1024)

            else -> ContextCompat.getColor(this, R.color.tile_empty)
        }
    }


    private fun moveLeft() {
        for (r in 0 until gridSize) {
            val row = grid[r].filter { it.value != 0 }
            var merged = BooleanArray(row.size)
            var newRow = mutableListOf<Tile>()

            var i = 0
            while (i < row.size) {
                if (i < row.size - 1 && row[i].value == row[i + 1].value && !merged[i] && !merged[i + 1]) {
                    addScore(row[i].value * 2)
                    newRow.add(Tile(row[i].value * 2))
                    merged[i] = true
                    merged[i + 1] = true
                    i += 2
                } else {
                    newRow.add(row[i])
                    i++
                }
            }

            while (newRow.size < gridSize) {
                newRow.add(Tile(0))
            }

            grid[r] = newRow.toTypedArray()
        }
        addRandomTile()
        updateUI()
    }

    private fun moveRight() {
        for (r in 0 until gridSize) {
            val row = grid[r].reversed().filter { it.value != 0 }
            var merged = BooleanArray(row.size)
            var newRow = mutableListOf<Tile>()

            var i = 0
            while (i < row.size) {
                if (i < row.size - 1 && row[i].value == row[i + 1].value && !merged[i] && !merged[i + 1]) {
                    addScore(row[i].value * 2)
                    newRow.add(Tile(row[i].value * 2))
                    merged[i] = true
                    merged[i + 1] = true
                    i += 2
                } else {
                    newRow.add(row[i])
                    i++
                }
            }

            while (newRow.size < gridSize) {
                newRow.add(Tile(0))
            }

            grid[r] = newRow.reversed().toTypedArray()
        }
        addRandomTile()
        updateUI()
    }


    private fun moveUp() {
        for (c in 0 until gridSize) {
            val column = mutableListOf<Tile>()
            for (r in 0 until gridSize) {
                column.add(grid[r][c])
            }

            val nonZeroTiles = column.filter { it.value != 0 }
            var merged = BooleanArray(nonZeroTiles.size)
            var newColumn = mutableListOf<Tile>()

            var i = 0
            while (i < nonZeroTiles.size) {
                if (i < nonZeroTiles.size - 1 && nonZeroTiles[i].value == nonZeroTiles[i + 1].value && !merged[i] && !merged[i + 1]) {
                    addScore(nonZeroTiles[i].value * 2)
                    newColumn.add(Tile(nonZeroTiles[i].value * 2))
                    merged[i] = true
                    merged[i + 1] = true
                    i += 2
                } else {
                    newColumn.add(nonZeroTiles[i])
                    i++
                }
            }

            while (newColumn.size < gridSize) {
                newColumn.add(Tile(0))
            }

            for (r in 0 until gridSize) {
                grid[r][c] = newColumn[r]
            }
        }
        addRandomTile()
        updateUI()
    }


    private fun moveDown() {
        for (c in 0 until gridSize) {
            val column = mutableListOf<Tile>()
            for (r in 0 until gridSize) {
                column.add(grid[r][c])
            }

            val nonZeroTiles = column.filter { it.value != 0 }
            var merged = BooleanArray(nonZeroTiles.size)
            var newColumn = mutableListOf<Tile>()

            var i = nonZeroTiles.size - 1
            while (i >= 0) {
                if (i > 0 && nonZeroTiles[i].value == nonZeroTiles[i - 1].value && !merged[i] && !merged[i - 1]) {
                    addScore(nonZeroTiles[i].value * 2)
                    newColumn.add(0, Tile(nonZeroTiles[i].value * 2))
                    merged[i] = true
                    merged[i - 1] = true
                    i -= 2
                } else {
                    newColumn.add(0, nonZeroTiles[i])
                    i--
                }
            }

            while (newColumn.size < gridSize) {
                newColumn.add(0, Tile(0))
            }

            for (r in 0 until gridSize) {
                grid[r][c] = newColumn[r]
            }
        }
        addRandomTile()
        updateUI()
    }
}

