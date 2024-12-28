package com.wishadish.feature.order.domain.model

data class DishDto(
    val dishId: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val isFavourite: Boolean? = null
)
