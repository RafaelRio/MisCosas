package com.rafario.miscosas.domain.repository

import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId

/**
 * Acceso al perfil remoto. No sustituye a UserRepository, que gestiona Room.
 */
interface RemoteUserProfileRepository {
    /**
     * Devuelve null solo si el servidor confirmó que el perfil no existe.
     * Los errores de conexión o del servidor deben propagarse como errores.
     */
    suspend fun findById(userId: UserId): User?

    /**
     * Crea o actualiza el perfil remoto identificado por user.id.
     */
    suspend fun upsert(user: User)
}
