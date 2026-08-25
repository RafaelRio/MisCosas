package com.rafario.miscosas.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.rafario.miscosas.data.local.database.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ItemDao {

    @Upsert
    suspend fun upsert(item: ItemEntity)

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun findById(id: String): ItemEntity?

    @Query(
        """
        SELECT * FROM items
        WHERE household_id = :householdId
        ORDER BY
            updated_at_epoch_seconds DESC,
            updated_at_nanoseconds DESC,
            id ASC
        """,
    )
    fun observeByHouseholdId(householdId: String): Flow<List<ItemEntity>>
}
