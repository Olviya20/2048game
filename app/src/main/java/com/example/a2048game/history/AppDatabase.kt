package com.example.a2048game.history
import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room
import androidx.room.TypeConverters

@Database(entities = [GameSession::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameSessionDao(): GameSessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "game_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

