package com.example.morningfocusalarm.di

import android.content.Context
import com.example.morningfocusalarm.data.local.PreferencesManager
import com.example.morningfocusalarm.data.repository.AlarmRepository
import com.example.morningfocusalarm.data.repository.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager = PreferencesManager(context)
    
    @Provides
    @Singleton
    fun provideAlarmRepository(
        @ApplicationContext context: Context,
        preferencesManager: PreferencesManager
    ): AlarmRepository = AlarmRepository(context, preferencesManager)
    
    @Provides
    @Singleton
    fun provideAppRepository(
        @ApplicationContext context: Context
    ): AppRepository = AppRepository(context)
}