package com.sp45.androidmanager.di

import android.content.Context
import androidx.room.Room
import com.sp45.androidmanager.data.database.AppDatabase
import com.sp45.androidmanager.data.database.SystemStatsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addMigrations(AppDatabase.MIGRATION_1_2) // Add migrations as needed
            .fallbackToDestructiveMigration() // For development only - remove in production
            .build()
    }

    @Provides
    fun provideSystemStatsDao(database: AppDatabase): SystemStatsDao {
        return database.systemStatsDao()
    }
}