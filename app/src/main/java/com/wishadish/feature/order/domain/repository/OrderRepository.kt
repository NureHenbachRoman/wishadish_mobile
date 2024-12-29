package com.wishadish.feature.order.domain.repository

import com.wishadish.feature.order.domain.model.Dish
import com.wishadish.feature.order.domain.model.DishDto
import com.wishadish.feature.order.domain.model.Order

interface OrderRepository {
    suspend fun getDishes(): List<DishDto>

    suspend fun getDishesWithFavourites(token: String): List <DishDto>

    suspend fun placeOrder(order: Order, token: String)

    suspend fun addToFavourites(dish: Dish, token: String)

    suspend fun removeFromFavourites(dish: Dish, token: String)
}