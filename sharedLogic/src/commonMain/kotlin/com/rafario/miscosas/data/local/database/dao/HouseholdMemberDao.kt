package com.rafario.miscosas.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.rafario.miscosas.data.local.database.entity.HouseholdMemberEntity

@Dao
internal interface HouseholdMemberDao {
    @Upsert
    suspend fun upsert(householdMember: HouseholdMemberEntity)

    @Query(
        "SELECT * FROM household_members " +
                "WHERE household_id = :householdId AND user_id = :userId"
    )
    suspend fun findByHouseholdIdAndUserId(householdId: String, userId: String): HouseholdMemberEntity?
}