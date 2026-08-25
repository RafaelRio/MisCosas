package com.rafario.miscosas.domain.repository

import com.rafario.miscosas.domain.model.HouseholdId
import com.rafario.miscosas.domain.model.Item
import com.rafario.miscosas.domain.model.ItemId
import kotlinx.coroutines.flow.Flow

internal interface ItemRepository {
    fun observeByHouseholdId(householdId: HouseholdId): Flow<List<Item>>

    suspend fun findById(itemId: ItemId): Item?

    suspend fun save(item: Item)
}
