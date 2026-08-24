package com.rafario.miscosas.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.rafario.miscosas.data.local.database.entity.DocumentEntity

@Dao
internal interface DocumentDao {
    @Upsert
    suspend fun upsert(document: DocumentEntity)

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun findById(id: String): DocumentEntity?
}