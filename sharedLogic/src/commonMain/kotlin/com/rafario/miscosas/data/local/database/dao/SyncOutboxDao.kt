package com.rafario.miscosas.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.rafario.miscosas.data.local.database.entity.SyncOutboxEntity

@Dao
internal interface SyncOutboxDao {

    @Upsert
    suspend fun upsert(syncOutbox: SyncOutboxEntity)

    @Query(
        """
        SELECT * FROM sync_outbox
        WHERE scope_id = :scopeId
          AND record_type_code = :recordTypeCode
          AND record_id = :recordId
        """,
    )
    suspend fun findByTarget(
        scopeId: String,
        recordTypeCode: String,
        recordId: String,
    ): SyncOutboxEntity?

    @Query(
        """
        SELECT * FROM sync_outbox
        ORDER BY
            enqueued_at_epoch_seconds ASC,
            enqueued_at_nanoseconds ASC,
            mutation_id ASC
        """,
    )
    suspend fun findAllPending(): List<SyncOutboxEntity>

    @Query(
        """
        DELETE FROM sync_outbox
        WHERE scope_id = :scopeId
          AND record_type_code = :recordTypeCode
          AND record_id = :recordId
          AND mutation_id = :mutationId
        """,
    )
    suspend fun deleteIfMutationMatches(
        scopeId: String,
        recordTypeCode: String,
        recordId: String,
        mutationId: String,
    ): Int
}
