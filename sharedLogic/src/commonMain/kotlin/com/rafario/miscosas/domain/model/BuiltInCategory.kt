package com.rafario.miscosas.domain.model

enum class BuiltInCategory(
    val id: CategoryId
) {
    TECHNOLOGY(
        id = CategoryId(value = "builtin:technology")
    ),
    APPLIANCES(
        id = CategoryId(value = "builtin:appliances")
    ),
    IMAGE_AND_SOUND(
        id = CategoryId(value = "builtin:image-and-sound")
    ),
    HOME(
        id = CategoryId(value = "builtin:home")
    ),
    KITCHEN(
        id = CategoryId(value = "builtin:kitchen")
    ),
    MOBILITY(
        id = CategoryId(value = "builtin:mobility")
    ),
    TOOLS(
        id = CategoryId(value = "builtin:tools")
    ),
    SPORTS(
        id = CategoryId(value = "builtin:sports")
    ),
    FASHION_AND_ACCESSORIES(
        id = CategoryId(value = "builtin:fashion-and-accessories")
    ),
    OTHER(
        id = CategoryId(value = "builtin:other")
    );

    companion object {
        fun fromIdOrNull(id: CategoryId): BuiltInCategory? {
            return entries.find { it.id == id }
        }
    }
}