package com.rafario.miscosas.data.local.database

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.rafario.miscosas.data.local.database.dao.DocumentDao
import com.rafario.miscosas.data.local.database.dao.HouseholdDao
import com.rafario.miscosas.data.local.database.dao.HouseholdMemberDao
import com.rafario.miscosas.data.local.database.dao.ItemDao
import com.rafario.miscosas.data.local.database.dao.MaintenanceRecordDao
import com.rafario.miscosas.data.local.database.dao.MaintenanceTaskDao
import com.rafario.miscosas.data.local.database.dao.ReturnPeriodDao
import com.rafario.miscosas.data.local.database.dao.UserDao
import com.rafario.miscosas.data.local.database.dao.WarrantyDao
import com.rafario.miscosas.data.local.database.entity.DocumentEntity
import com.rafario.miscosas.data.local.database.entity.HouseholdEntity
import com.rafario.miscosas.data.local.database.entity.HouseholdMemberEntity
import com.rafario.miscosas.data.local.database.entity.ItemEntity
import com.rafario.miscosas.data.local.database.entity.MaintenanceRecordEntity
import com.rafario.miscosas.data.local.database.entity.MaintenanceTaskEntity
import com.rafario.miscosas.data.local.database.entity.ReturnPeriodEntity
import com.rafario.miscosas.data.local.database.entity.UserEntity
import com.rafario.miscosas.data.local.database.entity.WarrantyEntity

@Database(
    entities = [
        UserEntity::class,
        HouseholdEntity::class,
        HouseholdMemberEntity::class,
        ItemEntity::class,
        DocumentEntity::class,
        WarrantyEntity::class,
        ReturnPeriodEntity::class,
        MaintenanceTaskEntity::class,
        MaintenanceRecordEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(MisCosasDatabaseConstructor::class)
internal abstract class MisCosasDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun householdDao(): HouseholdDao
    abstract fun householdMemberDao(): HouseholdMemberDao
    abstract fun itemDao(): ItemDao
    abstract fun documentDao(): DocumentDao
    abstract fun warrantyDao(): WarrantyDao
    abstract fun returnPeriodDao(): ReturnPeriodDao
    abstract fun maintenanceTaskDao(): MaintenanceTaskDao
    abstract fun maintenanceRecordDao(): MaintenanceRecordDao
}

@Suppress("KotlinNoActualForExpect")
internal expect object MisCosasDatabaseConstructor : RoomDatabaseConstructor<MisCosasDatabase> {

    override fun initialize(): MisCosasDatabase
}
