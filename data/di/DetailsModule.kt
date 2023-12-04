package com.ibm.beabee.data.di

import androidx.lifecycle.SavedStateHandle
import com.ibm.beabee.data.utils.RequestId
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object DetailsModule {
    @Provides
    @RequestId
    @ViewModelScoped
    fun provideRequestID(
        savedStateHandle: SavedStateHandle
    ): String? =
        savedStateHandle.get<String>("requestId")
}