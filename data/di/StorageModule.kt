package com.ibm.beabee.data.di

import  android.content.Context
import android.content.SharedPreferences
import com.ibm.beabee.data.datastorage.PreferenceManager
import com.ibm.beabee.data.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
    @Singleton
    @Provides
    fun providePreferenceManager(sharedPreferences: SharedPreferences) =
        PreferenceManager(sharedPreferences)

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(Constants.NAME, Context.MODE_PRIVATE)
}