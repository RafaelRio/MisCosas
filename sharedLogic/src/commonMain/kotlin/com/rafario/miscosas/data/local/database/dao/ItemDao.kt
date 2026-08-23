package com.rafario.miscosas.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.rafario.miscosas.data.local.database.entity.ItemEntity

@Dao
internal interface ItemDao {

    @Upsert
    suspend fun upsert(item: ItemEntity)

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun findById(id: String): ItemEntity?
}