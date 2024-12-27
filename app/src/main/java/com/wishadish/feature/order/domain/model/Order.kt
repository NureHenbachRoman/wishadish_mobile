package com.wishadish.feature.order.domain.model

import com.google.gson.annotations.SerializedName

data class Order(
    val cart: Map<Int, Int>,
    @SerializedName("deliveryTime") val pickupDateTime: String?
)