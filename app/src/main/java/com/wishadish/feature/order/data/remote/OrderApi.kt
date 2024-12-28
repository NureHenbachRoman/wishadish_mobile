package com.wishadish.feature.order.data.remote

import com.wishadish.feature.order.domain.model.Dish
import com.wishadish.feature.order.domain.model.DishDto
import com.wishadish.feature.order.domain.model.Order
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface OrderApi {
    @GET("dishes")
    suspend fun getDishes(): List<DishDto>

    @GET("dishes/user")
    suspend fun getDishesWithFavourites(
        @Header("token") token: String
    ): List<DishDto>

    @POST("orders")
    suspend fun placeOrder(
        @Body order: Order,
        @Header("token") token: String
    )

    @POST("favourites")
    suspend fun addToFavourites(
        @Body dishId: Int,
        @Header("token") token: String
    )

    @DELETE("favourites")
    suspend fun removeFromFavourites(
        @Body dishId: Int,
        @Header("token") token: String
    )
}