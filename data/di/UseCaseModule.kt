package com.ibm.beabee.data.di

import com.ibm.internship.beabee.domain.repositories.AnalyzeTextRepository
import com.ibm.internship.beabee.domain.repositories.AuthenticationRepository
import com.ibm.internship.beabee.domain.repositories.MessageRepository
import com.ibm.internship.beabee.domain.repositories.NotificationRepository
import com.ibm.internship.beabee.domain.repositories.RequestsRepository
import com.ibm.internship.beabee.domain.repositories.UserRepository
import com.ibm.internship.beabee.domain.usecases.AcceptRequestUseCase
import com.ibm.internship.beabee.domain.usecases.AnalyzeTextUseCase
import com.ibm.internship.beabee.domain.usecases.DeactivateAccountUseCase
import com.ibm.internship.beabee.domain.usecases.FinishRequestUseCase
import com.ibm.internship.beabee.domain.usecases.GetIdUserLoggedInUseCase
import com.ibm.internship.beabee.domain.usecases.ForgotPasswordUseCase
import com.ibm.internship.beabee.domain.usecases.GetIsUserLoggedInUseCase
import com.ibm.internship.beabee.domain.usecases.RequestDetailsUseCase
import com.ibm.internship.beabee.domain.usecases.GetMyRequestsUseCase
import com.ibm.internship.beabee.domain.usecases.GetRequestsUseCase
import com.ibm.internship.beabee.domain.usecases.GetUserUseCase
import com.ibm.internship.beabee.domain.usecases.GetMessagesUseCase
import com.ibm.internship.beabee.domain.usecases.GetNotificationsUseCase
import com.ibm.internship.beabee.domain.usecases.SubmitFeedbackUseCase
import com.ibm.internship.beabee.domain.usecases.UpdateUserUseCase
import com.ibm.internship.beabee.domain.usecases.UserLoginUseCase
import com.ibm.internship.beabee.domain.usecases.UserLogoutUseCase
import com.ibm.internship.beabee.domain.usecases.UserRegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Singleton
    @Provides
    fun provideLoginUseCase(repository: AuthenticationRepository) = UserLoginUseCase(repository)

    @Singleton
    @Provides
    fun provideGetCardsUseCase(requestsRepository: RequestsRepository): GetRequestsUseCase {
        return GetRequestsUseCase(requestsRepository)
    }

    @Singleton
    @Provides
    fun provideLogoutUseCase(repository: AuthenticationRepository) = UserLogoutUseCase(repository)

    @Singleton
    @Provides
    fun provideRequestsUseCase(repository: AuthenticationRepository) =
        UserRegisterUseCase(repository)

    @Singleton
    @Provides
    fun provideUpdateUserUseCase(repository: UserRepository) = UpdateUserUseCase(repository)

    @Singleton
    @Provides
    fun provideAnalyzeTextUseCase(repository: AnalyzeTextRepository) =
        AnalyzeTextUseCase(repository)

    @Singleton
    @Provides
    fun provideRequestDetailsUseCase(repository: RequestsRepository) =
        RequestDetailsUseCase(repository)

    @Singleton
    @Provides
    fun provideGetUserIdUseCase(repository: AuthenticationRepository) =
        GetIdUserLoggedInUseCase(repository)

    @Singleton
    @Provides
    fun provideGetUserDetailsUseCase(repository: UserRepository): GetUserUseCase =
        GetUserUseCase(repository)

    @Singleton
    @Provides
    fun provideGetIsUserLoggedInUseCase(repository: AuthenticationRepository): GetIsUserLoggedInUseCase =
        GetIsUserLoggedInUseCase(repository)

    @Singleton
    @Provides
    fun provideDeactivateAccountUseCase(repository: AuthenticationRepository): DeactivateAccountUseCase =
        DeactivateAccountUseCase(repository)

    @Singleton
    @Provides
    fun provideGetMyRequestsUseCase(repository: RequestsRepository): GetMyRequestsUseCase =
        GetMyRequestsUseCase(repository)

    @Singleton
    @Provides
    fun provideSubmitFeedbackUseCase(requestsRepository: RequestsRepository) =
        SubmitFeedbackUseCase(requestsRepository)

    @Singleton
    @Provides
    fun provideFinishRequestUseCase(requestsRepository: RequestsRepository) =
        FinishRequestUseCase(requestsRepository)

    @Singleton
    @Provides
    fun provideAcceptRequestUseCase(repository: RequestsRepository): AcceptRequestUseCase =
        AcceptRequestUseCase(repository)

    @Singleton
    @Provides
    fun provideGetNotificationsUseCase(notificationRepository: NotificationRepository) =
        GetNotificationsUseCase(notificationRepository)

    @Singleton
    @Provides
    fun provideGetMessagesUseCase(messageRepository: MessageRepository) =
        GetMessagesUseCase(messageRepository)

    @Singleton
    @Provides
    fun provideForgotPasswordUseCase(repository: AuthenticationRepository): ForgotPasswordUseCase =
        ForgotPasswordUseCase(repository)
}