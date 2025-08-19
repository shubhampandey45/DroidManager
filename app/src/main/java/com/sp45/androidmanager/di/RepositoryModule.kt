package com.sp45.androidmanager.di

import com.sp45.androidmanager.data.collector.SystemDataCollector
import com.sp45.androidmanager.data.repository.SystemStatsRepositoryImpl
import com.sp45.androidmanager.domain.repository.SystemStatsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSystemStatsRepository(
        systemStatsRepositoryImpl: SystemStatsRepositoryImpl
    ): SystemStatsRepository
}

@Module
@InstallIn(SingletonComponent::class)
object CollectorModule {

    @Provides
    @Singleton
    fun provideSystemDataCollector(): SystemDataCollector {
        return SystemDataCollector()
    }
}