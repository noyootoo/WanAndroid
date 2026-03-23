package com.example.wanandroid.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.wanandroid.WanApplication

@Database(entities = [ArticleEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    WanApplication.context,
                    AppDatabase::class.java,
                    "wanandroid_database"
                )
                .fallbackToDestructiveMigration() // 允许破坏性迁移，直接重建表
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
