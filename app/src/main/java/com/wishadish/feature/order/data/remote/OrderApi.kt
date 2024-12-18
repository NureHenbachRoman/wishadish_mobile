package com.wishadish.order.data.remote

import com.wishadish.order.domain.model.Dish
import retrofit2.http.GET

interface OrderApi {
    @GET("dishes")
    fun getDishes(): List<Dish>
}