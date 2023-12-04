package com.ibm.beabee.data.repositories

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.ibm.beabee.data.utils.InvalidEmailException
import com.ibm.beabee.utils.BeeLog
import com.ibm.internship.beabee.domain.repositories.AuthenticationRepository
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.domain.utils.UseCaseResponse
import com.ibm.internship.beabee.domain.utils.ValidationMessage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthenticationRepository {
    private val TAG = this::class.simpleName

    override suspend fun loginUser(
        email: String,
        password: String
    ): UseCaseResponse<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()?.let {
                BeeLog.d(TAG, "User is not null")
                return UseCaseResponse.Success(Unit)
            }
            BeeLog.e(TAG, "User is null")
            UseCaseResponse.Failure(ErrorMessage.GENERAL)
        } catch (networkException: FirebaseNetworkException) {
            networkException.getError(ErrorMessage.NO_NETWORK)
        } catch (invalidUserException: FirebaseAuthInvalidUserException) {
            invalidUserException.getError(ErrorMessage.INCORRECT_ACCOUNT)
        } catch (invalidUserException: FirebaseAuthInvalidCredentialsException) {
            invalidUserException.getError(ErrorMessage.INCORRECT_ACCOUNT)
        } catch (exception: Exception) {
            exception.getError(ErrorMessage.GENERAL)
        }
    }

    override suspend fun logoutUser(): UseCaseResponse<Unit> {
        return try {
            firebaseAuth.signOut()
            BeeLog.e(TAG, "User logout successful")
            UseCaseResponse.Success(Unit)
        } catch (networkException: FirebaseNetworkException) {
            networkException.getError(ErrorMessage.NO_NETWORK)
        } catch (exception: Exception) {
            exception.getError(ErrorMessage.GENERAL)
        }
    }

    override suspend fun registerUser(
        email: String,
        password: String
    ): UseCaseResponse<String> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()?.let { response ->
                response.user?.let { user ->
                    BeeLog.e(TAG, "Register was successful")
                    return UseCaseResponse.Success(user.uid)
                }
            }
            BeeLog.e(TAG, "User is null")
            return UseCaseResponse.Failure(ErrorMessage.GENERAL)
        } catch (networkException: FirebaseNetworkException) {
            networkException.getError(ErrorMessage.NO_NETWORK)
        } catch (invalidEmail: FirebaseAuthUserCollisionException) {
            invalidEmail.getError(ErrorMessage.INVALID_EMAIL)
        } catch (exception: Exception) {
            exception.getError(ErrorMessage.GENERAL)
        }
    }

    override suspend fun deactivateAccount(): UseCaseResponse<Unit> {
        return try {
            firebaseAuth.currentUser?.let {
                it.delete()
                firebaseAuth.signOut()
                BeeLog.e(TAG, "User account deactivation successful")
                return UseCaseResponse.Success(Unit)
            }
            BeeLog.e(TAG, "User is null")
            UseCaseResponse.Failure(ErrorMessage.GENERAL)
        } catch (networkException: FirebaseNetworkException) {
            networkException.getError(ErrorMessage.NO_NETWORK)
        } catch (exception: Exception) {
            exception.getError(ErrorMessage.GENERAL)
        }
    }

    override fun getIdUserLoggedIn(): UseCaseResponse<String> = firebaseAuth.currentUser?.uid?.let {
        BeeLog.d(TAG, "Get id success")
        UseCaseResponse.Success(it)
    } ?: run {
        BeeLog.e(TAG, "User is null")
        UseCaseResponse.Failure(ErrorMessage.INVALID_USER)
    }

    override fun getIsUserLoggedIn() = firebaseAuth.currentUser != null

    override fun forgotPassword(email: String, validationMessage: ValidationMessage)
            : UseCaseResponse<String> {
        return try {
            if (validationMessage == ValidationMessage.EMAIL_INVALID) {
                throw InvalidEmailException()
            }
            firebaseAuth.sendPasswordResetEmail(email)
            return UseCaseResponse.Success("An email has been sent")
        } catch (networkException: FirebaseNetworkException) {
            networkException.getError(ErrorMessage.NO_NETWORK)
        } catch (emailException: FirebaseAuthEmailException) {
            emailException.getError(ErrorMessage.INVALID_EMAIL)
        } catch (validationException: InvalidEmailException) {
            validationException.getError(ErrorMessage.INCORRECT_EMAIL)
        }
    }

    private fun java.lang.Exception.getError(error: ErrorMessage): UseCaseResponse.Failure {
        BeeLog.e(TAG, "Authentication failure. Exception: ${this.message}")
        return UseCaseResponse.Failure(error)
    }
}