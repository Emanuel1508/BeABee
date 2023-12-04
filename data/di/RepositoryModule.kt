package com.ibm.beabee.data.di

import com.ibm.beabee.data.repositories.AnalyzeTextRepositoryImpl
import com.ibm.beabee.data.repositories.AuthenticationRepositoryImpl
import com.ibm.beabee.data.repositories.MessageRepositoryImpl
import com.ibm.beabee.data.repositories.NotificationRepositoryImpl
import com.ibm.beabee.data.repositories.RequestsRepositoryImpl
import com.ibm.beabee.data.repositories.UserRepositoryImpl
import com.ibm.internship.beabee.domain.repositories.AnalyzeTextRepository
import com.ibm.internship.beabee.domain.repositories.AuthenticationRepository
import com.ibm.internship.beabee.domain.repositories.MessageRepository
import com.ibm.internship.beabee.domain.repositories.NotificationRepository
import com.ibm.internship.beabee.domain.repositories.RequestsRepository
import com.ibm.internship.beabee.domain.repositories.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthenticationRepository(authenticationRepositoryImpl: AuthenticationRepositoryImpl): AuthenticationRepository

    @Binds
    abstract fun bindAnalyzeTextRepository(analyzeTextRepository: AnalyzeTextRepositoryImpl): AnalyzeTextRepository

    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindRequestRepository(requestRepository: RequestsRepositoryImpl): RequestsRepository

    @Binds
    abstract fun bindNotificationRepository(notificationRepository: NotificationRepositoryImpl): NotificationRepository

    @Binds
    abstract fun bindMessageRepository(messageRepository: MessageRepositoryImpl): MessageRepository
}