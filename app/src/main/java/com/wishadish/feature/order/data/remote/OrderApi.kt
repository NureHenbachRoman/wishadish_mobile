package com.wishadish.feature.order.data.remote

import com.wishadish.feature.order.domain.model.Dish
import com.wishadish.feature.order.domain.model.Order
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface OrderApi {
    @GET("dishes")
    suspend fun getDishes(): List<Dish>

    @POST("orders")
    suspend fun placeOrder(
        @Body order: Order,
        @Header("token") token: String
    )
}