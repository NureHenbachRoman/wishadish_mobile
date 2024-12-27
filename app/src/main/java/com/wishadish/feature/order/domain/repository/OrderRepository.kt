package com.wishadish.feature.order.domain.repository

import com.wishadish.feature.order.domain.model.Dish
import com.wishadish.feature.order.domain.model.Order

interface OrderRepository {
    suspend fun getDishes(): List<Dish>

    suspend fun placeOrder(order: Order, token: String)
}