package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReturnTrackingStateTest {

    @Test
    fun exposesStableCodes() {
        assertEquals("tracking", ReturnTrackingState.TRACKING.code)
        assertEquals("returned", ReturnTrackingState.RETURNED.code)
        assertEquals("kept", ReturnTrackingState.KEPT.code)
    }

    @Test
    fun findsStateByCode() {
        assertEquals(ReturnTrackingState.TRACKING, ReturnTrackingState.fromCodeOrNull("tracking"))
        assertEquals(ReturnTrackingState.KEPT, ReturnTrackingState.fromCodeOrNull("kept"))
        assertEquals(ReturnTrackingState.RETURNED, ReturnTrackingState.fromCodeOrNull("returned"))
    }

    @Test
    fun returnsNullForUnknownCode() {
        assertNull(
            ReturnTrackingState.fromCodeOrNull("expired")
        )
    }
}