package com.rafario.miscosas.data.auth

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.rafario.miscosas.api.auth.NativeAuthenticationException
import com.rafario.miscosas.api.auth.NativeAuthenticationFailure
import com.rafario.miscosas.api.auth.NativeAuthenticationProvider
import com.rafario.miscosas.api.auth.NativeSessionLookupResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class FirebaseNativeAuthenticationProvider(
    private val firebaseAuth: FirebaseAuth
) : NativeAuthenticationProvider {

    override suspend fun getCurrentSession(): NativeSessionLookupResult {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            try {
                currentUser.reload().await()
                return NativeSessionLookupResult.Authenticated(
                    userId = currentUser.uid,
                    isEmailVerified = currentUser.isEmailVerified
                )
            } catch (_: FirebaseAuthInvalidUserException) {
                return NativeSessionLookupResult.NoSession
            } catch (e: CancellationException) {
                throw e
            } catch (_: FirebaseException) {
                return NativeSessionLookupResult.RecoverableError
            }
        } else {
            return NativeSessionLookupResult.NoSession
        }
    }

    override suspend fun signInWithEmail(
        email: String,
        password: String,
    ): String {
        return try {
            val user = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()
                .user
                ?: throw NativeAuthenticationException(
                    failure = NativeAuthenticationFailure.UNEXPECTED,
                )

            user.uid
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (exception: FirebaseException) {
            throw NativeAuthenticationException(
                failure = exception.toNativeAuthenticationFailure()
            )
        }
    }

    override suspend fun registerWithEmail(
        email: String,
        password: String
    ): String {
        return try {
            val user = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()
                .user
                ?: throw NativeAuthenticationException(
                    failure = NativeAuthenticationFailure.UNEXPECTED,
                )

            user.uid
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (exception: FirebaseException) {
            throw NativeAuthenticationException(
                failure = exception.toNativeAuthenticationFailure()
            )
        }
    }
}

private fun FirebaseException.toNativeAuthenticationFailure(): NativeAuthenticationFailure {
    return when (this) {
        is FirebaseNetworkException -> NativeAuthenticationFailure.TEMPORARY
        is FirebaseTooManyRequestsException -> NativeAuthenticationFailure.RATE_LIMITED
        is FirebaseAuthInvalidCredentialsException -> NativeAuthenticationFailure.CREDENTIALS_REJECTED
        is FirebaseAuthInvalidUserException -> NativeAuthenticationFailure.CREDENTIALS_REJECTED
        is FirebaseAuthUserCollisionException -> NativeAuthenticationFailure.CREDENTIALS_REJECTED
        else -> NativeAuthenticationFailure.UNEXPECTED
    }
}
