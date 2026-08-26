package com.rafario.miscosas.data.repository

import androidx.room3.withWriteTransaction
import com.rafario.miscosas.data.local.database.MisCosasDatabase
import com.rafario.miscosas.data.local.database.entity.SyncOutboxEntity
import com.rafario.miscosas.data.local.database.mapper.toDomain
import com.rafario.miscosas.data.local.database.mapper.toEntity
import com.rafario.miscosas.data.sync.model.SyncOperation
import com.rafario.miscosas.data.sync.model.SyncRecordType
import com.rafario.miscosas.domain.model.HouseholdId
import com.rafario.miscosas.domain.model.Item
import com.rafario.miscosas.domain.model.ItemId
import com.rafario.miscosas.domain.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.uuid.Uuid

internal class RoomItemRepository(
    private val database: MisCosasDatabase,
    private val clock: Clock = Clock.System,
    private val generateMutationId: () -> String = {
        Uuid.random().toString()
    },
): ItemRepository {

    override suspend fun findById(itemId: ItemId): Item? {
        return database.itemDao().findById(itemId.value)?.toDomain()
    }

    override fun observeByHouseholdId(householdId: HouseholdId): Flow<List<Item>> {
        return database
            .itemDao()
            .observeByHouseholdId(householdId.value)
            .map { itemEntities ->
                itemEntities.map { itemEntity ->
                    itemEntity.toDomain()
                }
            }
    }

    override suspend fun save(item: Item) {
        val itemEntity = item.toEntity()
        val enqueuedAt = clock.now()
        val mutationId = generateMutationId()

        val syncOutboxEntity = SyncOutboxEntity(
            scopeId = item.householdId.value,
            recordTypeCode = SyncRecordType.ITEM.code,
            recordId = item.id.value,
            mutationId = mutationId,
            operationCode = SyncOperation.UPSERT.code,
            baseRemoteVersion = null,
            enqueuedAtEpochSeconds = enqueuedAt.epochSeconds,
            enqueuedAtNanoseconds = enqueuedAt.nanosecondsOfSecond,
        )

        database.withWriteTransaction {
            database.itemDao().upsert(itemEntity)
            database.syncOutboxDao().upsert(syncOutboxEntity)
        }
    }
}
