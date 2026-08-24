package com.rafario.miscosas.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.rafario.miscosas.data.local.database.entity.MaintenanceTaskEntity

@Dao
internal interface MaintenanceTaskDao {
    @Upsert
    suspend fun upsert(maintenanceTask: MaintenanceTaskEntity)

    @Query("SELECT * FROM maintenance_tasks WHERE id = :id")
    suspend fun findById(id: String): MaintenanceTaskEntity?
}
