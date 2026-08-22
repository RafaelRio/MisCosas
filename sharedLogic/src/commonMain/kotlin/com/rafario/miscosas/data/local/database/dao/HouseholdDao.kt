package com.rafario.miscosas.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.rafario.miscosas.data.local.database.entity.HouseholdEntity

@Dao
internal interface HouseholdDao {

    @Upsert
    suspend fun upsert(household: HouseholdEntity)

    @Query("SELECT * FROM households WHERE id = :id")
    suspend fun findById(id: String): HouseholdEntity?
}