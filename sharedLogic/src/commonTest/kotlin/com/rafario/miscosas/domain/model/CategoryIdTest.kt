package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CategoryIdTest {

    @Test
    fun acceptsStableBuiltInCategoryId() {
        val id = CategoryId(
            value = "builtin:technology",
        )

        assertEquals("builtin:technology", id.value)
    }

    @Test
    fun rejectsBlankCategoryId() {
        assertFailsWith<IllegalArgumentException> {
            CategoryId(
                value = "   ",
            )
        }
    }

    @Test
    fun rejectsCategoryIdWithSurroundingWhitespace() {
        assertFailsWith<IllegalArgumentException> {
            CategoryId(
                value = " builtin:technology ",
            )
        }
    }
}
