package com.rafario.miscosas.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.rafario.miscosas.data.local.database.entity.ItemHistoryEventEntity

@Dao
internal interface ItemHistoryEventDao {
    @Insert
    suspend fun insert(itemHistoryEvent: ItemHistoryEventEntity)

    @Query("SELECT * FROM item_history_events WHERE id = :id")
    suspend fun findById(id: String): ItemHistoryEventEntity?

    @Query(
        """
        SELECT * FROM item_history_events
        WHERE item_id = :itemId
        ORDER BY
            occurred_at_epoch_seconds DESC,
            occurred_at_nanoseconds DESC,
            id DESC
        """,
    )
    suspend fun findAllByItemId(itemId: String): List<ItemHistoryEventEntity>
}
