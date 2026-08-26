package com.rafario.miscosas.data.repository

import androidx.room3.withWriteTransaction
import com.rafario.miscosas.data.local.database.MisCosasDatabase
import com.rafario.miscosas.data.local.database.entity.SyncOutboxEntity
import com.rafario.miscosas.data.local.database.mapper.toEntity
import com.rafario.miscosas.data.sync.model.SyncOperation
import com.rafario.miscosas.data.sync.model.SyncRecordType
import com.rafario.miscosas.domain.model.Household
import com.rafario.miscosas.domain.model.HouseholdMember
import com.rafario.miscosas.domain.repository.HouseholdRepository
import kotlin.time.Clock
import kotlin.uuid.Uuid

internal class RoomHouseholdRepository(
    private val database: MisCosasDatabase,
    private val clock: Clock = Clock.System,
    private val generateMutationId: () -> String = {
        Uuid.random().toString()
    },
) : HouseholdRepository {

    override suspend fun create(
        household: Household,
        ownerMembership: HouseholdMember,
    ) {
        val householdEntity = household.toEntity()
        val ownerMembershipEntity = ownerMembership.toEntity()

        val enqueuedAt = clock.now()
        val householdMutationId = generateMutationId()
        val membershipMutationId = generateMutationId()

        val householdSyncOutbox = SyncOutboxEntity(
            scopeId = householdEntity.id,
            recordTypeCode = SyncRecordType.HOUSEHOLD.code,
            recordId = householdEntity.id,
            mutationId = householdMutationId,
            operationCode = SyncOperation.UPSERT.code,
            baseRemoteVersion = null,
            enqueuedAtEpochSeconds = enqueuedAt.epochSeconds,
            enqueuedAtNanoseconds = enqueuedAt.nanosecondsOfSecond,
        )

        val membershipSyncOutbox = SyncOutboxEntity(
            scopeId = householdEntity.id,
            recordTypeCode = SyncRecordType.HOUSEHOLD_MEMBER.code,
            recordId = ownerMembershipEntity.userId,
            mutationId = membershipMutationId,
            operationCode = SyncOperation.UPSERT.code,
            baseRemoteVersion = null,
            enqueuedAtEpochSeconds = enqueuedAt.epochSeconds,
            enqueuedAtNanoseconds = enqueuedAt.nanosecondsOfSecond,
        )

        database.withWriteTransaction {
            database.householdDao().upsert(householdEntity)
            database.householdMemberDao().upsert(ownerMembershipEntity)
            database.syncOutboxDao().upsert(householdSyncOutbox)
            database.syncOutboxDao().upsert(membershipSyncOutbox)
        }
    }
}
