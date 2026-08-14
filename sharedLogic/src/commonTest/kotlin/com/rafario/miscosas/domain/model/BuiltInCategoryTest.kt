package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BuiltInCategoryTest {

    @Test
    fun exposesExpectedIdsInDisplayOrder() {
        val ids = BuiltInCategory.entries.map { category ->
            category.id.value
        }

        assertEquals(
            listOf(
                "builtin:technology",
                "builtin:appliances",
                "builtin:image-and-sound",
                "builtin:home",
                "builtin:kitchen",
                "builtin:mobility",
                "builtin:tools",
                "builtin:sports",
                "builtin:fashion-and-accessories",
                "builtin:other",
            ),
            ids,
        )
    }

    @Test
    fun findsBuiltInCategoryById() {
        val category = BuiltInCategory.fromIdOrNull(
            CategoryId("builtin:technology")
        )

        assertEquals(BuiltInCategory.TECHNOLOGY, category)
    }

    @Test
    fun returnsNullForCustomCategoryId() {
        val category = BuiltInCategory.fromIdOrNull(
            CategoryId(
                "custom:550e8400-e29b-41d4-a716-446655440000"
            )
        )

        assertNull(category)
    }
}