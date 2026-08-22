package com.rafario.miscosas.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.rafario.miscosas.data.local.database.entity.UserEntity

@Dao
internal interface UserDao {

    @Upsert
    suspend fun upsert(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun findById(id: String): UserEntity?
}