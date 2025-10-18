package com.sp45.androidmanager.di

import android.content.Context
import com.sp45.androidmanager.data.service.AlertNotificationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    fun provideAlertNotificationManager(@ApplicationContext context: Context): AlertNotificationManager {
        return AlertNotificationManager(context)
    }
}
