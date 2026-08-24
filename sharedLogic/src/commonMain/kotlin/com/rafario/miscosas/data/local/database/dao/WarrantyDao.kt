package com.rafario.miscosas.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.rafario.miscosas.data.local.database.entity.WarrantyEntity

@Dao
internal interface WarrantyDao {

    @Upsert
    suspend fun upsert(warranty: WarrantyEntity)

    @Query("SELECT * FROM warranties WHERE id = :id")
    suspend fun findById(id: String): WarrantyEntity?
}
