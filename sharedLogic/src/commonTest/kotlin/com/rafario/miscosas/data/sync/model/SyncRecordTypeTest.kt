package com.rafario.miscosas.data.sync.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SyncRecordTypeTest {

    @Test
    fun hasStableCodes() {
        assertEquals("user", SyncRecordType.USER.code)
        assertEquals("household", SyncRecordType.HOUSEHOLD.code)
        assertEquals("household_member", SyncRecordType.HOUSEHOLD_MEMBER.code)
        assertEquals("item", SyncRecordType.ITEM.code)
        assertEquals("document", SyncRecordType.DOCUMENT.code)
        assertEquals("warranty", SyncRecordType.WARRANTY.code)
        assertEquals("return_period", SyncRecordType.RETURN_PERIOD.code)
        assertEquals("maintenance_task", SyncRecordType.MAINTENANCE_TASK.code)
        assertEquals("maintenance_record", SyncRecordType.MAINTENANCE_RECORD.code)
        assertEquals("item_history_event", SyncRecordType.ITEM_HISTORY_EVENT.code)
    }

    @Test
    fun findsTypeByCode() {
        assertEquals(SyncRecordType.USER, SyncRecordType.fromCodeOrNull("user"))
        assertEquals(SyncRecordType.HOUSEHOLD, SyncRecordType.fromCodeOrNull("household"))
        assertEquals(SyncRecordType.HOUSEHOLD_MEMBER, SyncRecordType.fromCodeOrNull("household_member"))
        assertEquals(SyncRecordType.ITEM, SyncRecordType.fromCodeOrNull("item"))
        assertEquals(SyncRecordType.DOCUMENT, SyncRecordType.fromCodeOrNull("document"))
        assertEquals(SyncRecordType.WARRANTY, SyncRecordType.fromCodeOrNull("warranty"))
        assertEquals(SyncRecordType.RETURN_PERIOD, SyncRecordType.fromCodeOrNull("return_period"))
        assertEquals(SyncRecordType.MAINTENANCE_TASK, SyncRecordType.fromCodeOrNull("maintenance_task"))
        assertEquals(SyncRecordType.MAINTENANCE_RECORD, SyncRecordType.fromCodeOrNull("maintenance_record"))
        assertEquals(SyncRecordType.ITEM_HISTORY_EVENT, SyncRecordType.fromCodeOrNull("item_history_event"))
    }

    @Test
    fun returnsNullForUnknownCode() {
        assertNull(SyncRecordType.fromCodeOrNull("unknown"))
    }
}