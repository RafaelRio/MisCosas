package com.rafario.miscosas.data.local.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index

@Entity(
    tableName = "sync_outbox",
    primaryKeys = ["scope_id", "record_type_code", "record_id"],
    indices = [
        Index(value = ["mutation_id"], unique = true),
        Index(
            value = [
                "enqueued_at_epoch_seconds",
                "enqueued_at_nanoseconds",
                "mutation_id",
            ],
        ),
    ],
)
internal data class SyncOutboxEntity(
    @ColumnInfo(name = "scope_id")
    val scopeId: String,
    @ColumnInfo(name = "record_type_code")
    val recordTypeCode: String,
    @ColumnInfo(name = "record_id")
    val recordId: String,
    @ColumnInfo(name = "mutation_id")
    val mutationId: String,
    @ColumnInfo(name = "operation_code")
    val operationCode: String,
    @ColumnInfo(name = "base_remote_version")
    val baseRemoteVersion: Long?,
    @ColumnInfo(name = "enqueued_at_epoch_seconds")
    val enqueuedAtEpochSeconds: Long,
    @ColumnInfo(name = "enqueued_at_nanoseconds")
    val enqueuedAtNanoseconds: Int,
)
