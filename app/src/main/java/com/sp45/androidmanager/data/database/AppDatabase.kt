package com.sp45.androidmanager.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [SystemStatsEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun systemStatsDao(): SystemStatsDao

    companion object {
        const val DATABASE_NAME = "system_monitor_db"

       //  Migration example (for future schema changes)
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Example: database.execSQL("ALTER TABLE system_stats ADD COLUMN newColumn TEXT")
            }
        }
    }
}