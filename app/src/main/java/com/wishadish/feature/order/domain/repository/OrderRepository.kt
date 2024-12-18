package com.wishadish.order.domain.repository

import com.wishadish.order.domain.model.Dish

interface OrderRepository {
    fun getDishes(): List<Dish>
    fun getDishesByCategory(category: String): List<Dish>
    fun getAllCategories(): List<String>
}