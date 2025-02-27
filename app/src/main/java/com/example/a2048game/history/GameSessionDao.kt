package com.example.a2048game.history
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameSessionDao {
    @Insert
    suspend fun insertSession(session: GameSession): Long

    @Query("SELECT * FROM game_sessions ORDER BY date DESC")
    suspend fun getAllSessions(): List<GameSession>
}
