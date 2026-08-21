package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HouseholdRoleTest {

    @Test
    fun hasStableCodes() {
        assertEquals("owner", HouseholdRole.OWNER.code)
        assertEquals("member", HouseholdRole.MEMBER.code)
    }

    @Test
    fun findsHouseholdRoleByCode() {
        val role = HouseholdRole.fromCodeOrNull("member")

        assertEquals(HouseholdRole.MEMBER, role)
    }

    @Test
    fun returnsNullForUnknownCode() {
        val role = HouseholdRole.fromCodeOrNull("unknown")

        assertNull(role)
    }
}