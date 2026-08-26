package com.rafario.miscosas.data.repository

import androidx.room3.withWriteTransaction
import com.rafario.miscosas.data.local.database.MisCosasDatabase
import com.rafario.miscosas.data.local.database.entity.SyncOutboxEntity
import com.rafario.miscosas.data.local.database.mapper.toEntity
import com.rafario.miscosas.data.sync.model.SyncOperation
import com.rafario.miscosas.data.sync.model.SyncRecordType
import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.repository.UserRepository
import kotlin.time.Clock
import kotlin.uuid.Uuid

internal class RoomUserRepository(
    private val database: MisCosasDatabase,
    private val clock: Clock = Clock.System,
    private val generateMutationId: () -> String = {
        Uuid.random().toString()
    },
) : UserRepository {

    override suspend fun save(user: User) {
        val userEntity = user.toEntity()
        val enqueuedAt = clock.now()
        val mutationId = generateMutationId()

        val syncOutboxEntity = SyncOutboxEntity(
            scopeId = userEntity.id,
            recordTypeCode = SyncRecordType.USER.code,
            recordId = userEntity.id,
            mutationId = mutationId,
            operationCode = SyncOperation.UPSERT.code,
            baseRemoteVersion = null,
            enqueuedAtEpochSeconds = enqueuedAt.epochSeconds,
            enqueuedAtNanoseconds = enqueuedAt.nanosecondsOfSecond,
        )

        database.withWriteTransaction {
            database.userDao().upsert(userEntity)
            database.syncOutboxDao().upsert(syncOutboxEntity)
        }
    }
}
