package com.rafario.miscosas.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.rafario.miscosas.data.local.database.entity.ReturnPeriodEntity

@Dao
internal interface ReturnPeriodDao {
    @Upsert
    suspend fun upsert(returnPeriod: ReturnPeriodEntity)

    @Query("SELECT * FROM return_periods WHERE item_id = :itemId")
    suspend fun findByItemId(itemId: String): ReturnPeriodEntity?
}