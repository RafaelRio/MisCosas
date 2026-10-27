package com.rafario.miscosas.api.auth

interface NativeAuthenticationProvider {

    suspend fun getCurrentSession(): NativeSessionLookupResult

    suspend fun signInWithEmail(email: String, password: String): String

    suspend fun registerWithEmail(email: String, password: String): String
}