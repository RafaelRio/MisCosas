package com.rafario.miscosas.data.local.database

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.rafario.miscosas.data.local.database.dao.DocumentDao
import com.rafario.miscosas.data.local.database.dao.HouseholdDao
import com.rafario.miscosas.data.local.database.dao.HouseholdMemberDao
import com.rafario.miscosas.data.local.database.dao.ItemDao
import com.rafario.miscosas.data.local.database.dao.UserDao
import com.rafario.miscosas.data.local.database.entity.DocumentEntity
import com.rafario.miscosas.data.local.database.entity.HouseholdEntity
import com.rafario.miscosas.data.local.database.entity.HouseholdMemberEntity
import com.rafario.miscosas.data.local.database.entity.ItemEntity
import com.rafario.miscosas.data.local.database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        HouseholdEntity::class,
        HouseholdMemberEntity::class,
        ItemEntity::class,
        DocumentEntity::class,
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
}

@Suppress("KotlinNoActualForExpect")
internal expect object MisCosasDatabaseConstructor : RoomDatabaseConstructor<MisCosasDatabase> {

    override fun initialize(): MisCosasDatabase
}