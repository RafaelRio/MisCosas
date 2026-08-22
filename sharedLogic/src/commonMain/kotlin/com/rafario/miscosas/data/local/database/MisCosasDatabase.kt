package com.rafario.miscosas.data.local.database

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.rafario.miscosas.data.local.database.dao.HouseholdDao
import com.rafario.miscosas.data.local.database.dao.UserDao
import com.rafario.miscosas.data.local.database.entity.HouseholdEntity
import com.rafario.miscosas.data.local.database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        HouseholdEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(MisCosasDatabaseConstructor::class)
internal abstract class MisCosasDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun householdDao(): HouseholdDao
}

@Suppress("KotlinNoActualForExpect")
internal expect object MisCosasDatabaseConstructor :
    RoomDatabaseConstructor<MisCosasDatabase> {

    override fun initialize(): MisCosasDatabase
}