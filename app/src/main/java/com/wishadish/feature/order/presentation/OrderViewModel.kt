package com.wishadish.feature.order.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.wishadish.feature.order.domain.model.CartItem
import com.wishadish.feature.order.domain.model.Dish
import com.wishadish.feature.order.domain.model.Order
import com.wishadish.feature.order.domain.repository.OrderRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

const val IMAGE_LINK_PREFIX = "http://10.0.2.2:5000"
class OrderViewModel(private val repository: OrderRepository) : ViewModel() {

    val cartItems = mutableStateListOf<CartItem>()
    val customPickupTime = mutableStateOf(false)
    val pickupDateTime = mutableStateOf(LocalDateTime.now().plusMinutes(20))
    val showTimeError = mutableStateOf(false)

    private var allDishes: List<Dish> by mutableStateOf(emptyList())

    var searchQuery by mutableStateOf("")
        private set

    var displayedDishes: List<Dish> by mutableStateOf(emptyList())
        private set

    init {
        Firebase.auth.addAuthStateListener {
            viewModelScope.launch {
                fetchDishes()
            }
        }
        viewModelScope.launch {
            fetchDishes()
        }
    }

    private suspend fun fetchDishes() {
        try {
            println("USER: " + Firebase.auth.currentUser)
            val dtoList = if (Firebase.auth.currentUser == null)
                repository.getDishes()
            else
                repository.getDishesWithFavourites(Firebase.auth.currentUser!!.getIdToken(false).await().token!!)

            allDishes = dtoList.map { dto ->
                Dish(
                    dishId = dto.dishId,
                    name = dto.name,
                    description = dto.description,
                    price = dto.price,
                    imageUrl = dto.imageUrl,
                    category = dto.category,
                    isFavourite = mutableStateOf(dto.isFavourite)
                )
            }
            displayedDishes = allDishes
            println("NOT EXCEPTION BLABLA")
            println(allDishes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun placeOrder(onSuccess: () -> Unit) {
        val resultPickupDateTime: String?
        if (customPickupTime.value) {
            val zonedDateTime = pickupDateTime.value.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC)
            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            resultPickupDateTime = zonedDateTime.format(dateTimeFormatter)
        } else {
            resultPickupDateTime = null
        }
        val order = Order(
            cartItems.associate { it.dish.dishId to it.quantity },
            resultPickupDateTime
        )
        viewModelScope.launch {
            repository.placeOrder(order, Firebase.auth.currentUser?.getIdToken(false)?.await()?.token!!)
        }
        onSuccess()
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        displayedDishes = if (query.isBlank()) {
            allDishes
        } else {
            allDishes.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    fun getAllCategories(): List<String> {
        println("GETTING CATEGORIES")
        println(allDishes)
        return allDishes.map { it.category }.distinct()
    }

    fun getDishesByCategory(category: String): List<Dish> {
        return displayedDishes.filter { it.category == category }
    }

    fun addDishToCart(dish: Dish) {
        val existingItem = cartItems.find { it.dish.dishId == dish.dishId }
        if (existingItem != null) {
            increaseCartItemQuantity(existingItem)
        } else {
            cartItems.add(CartItem(dish, 1))
        }
    }

    fun removeDishFromCart(cartItem: CartItem) {
        cartItems.remove(cartItem)
    }

    fun increaseCartItemQuantity(cartItem: CartItem) {
        val updatedCartItem = cartItem.copy(quantity = cartItem.quantity + 1)
        updateCartItem(cartItem, updatedCartItem)
    }

    fun decreaseCartItemQuantity(cartItem: CartItem) {
        if (cartItem.quantity > 1) {
            val updatedCartItem = cartItem.copy(quantity = cartItem.quantity - 1)
            updateCartItem(cartItem, updatedCartItem)
        } else {
            cartItems.remove(cartItem)
        }
    }

    fun toggleFavourite(dish: Dish) {
        if (dish.isFavourite.value!!)
            removeFromFavourites(dish)
        else
            addToFavourites(dish)
    }
    private fun addToFavourites(dish: Dish) {
        dish.isFavourite.value = true
        viewModelScope.launch {
            repository.addToFavourites(dish, Firebase.auth.currentUser?.getIdToken(false)?.await()?.token!!)
        }
    }

    private fun removeFromFavourites(dish: Dish) {
        dish.isFavourite.value = false
        viewModelScope.launch {
            repository.removeFromFavourites(dish, Firebase.auth.currentUser?.getIdToken(false)?.await()?.token!!)
        }
    }

    private fun updateCartItem(oldItem: CartItem, newItem: CartItem) {
        val index = cartItems.indexOf(oldItem)
        if (index != -1) {
            cartItems[index] = newItem
        }
    }
}