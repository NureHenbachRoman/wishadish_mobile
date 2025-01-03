package com.wishadish.feature.order.domain.model

import androidx.compose.runtime.MutableState

data class Dish(
    val dishId: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    var isFavourite: MutableState<Boolean?>
)
