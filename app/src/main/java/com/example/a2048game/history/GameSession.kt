package com.example.a2048game.history
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_sessions")
data class GameSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @ColumnInfo(name = "score")
    val score: Int,

    @ColumnInfo(name = "date")
    val date: Long
)
