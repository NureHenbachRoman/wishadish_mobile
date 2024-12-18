package com.wishadish.order.domain.model

data class Dish(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String
)
