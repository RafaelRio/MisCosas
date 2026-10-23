package com.rafario.miscosas.api.auth

interface NativeAuthenticationProvider {

    suspend fun getCurrentUserId(): String?
}