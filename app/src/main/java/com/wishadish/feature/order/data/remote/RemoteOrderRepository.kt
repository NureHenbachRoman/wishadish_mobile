package com.wishadish.feature.order.data.remote

import com.wishadish.feature.order.domain.model.Dish
import com.wishadish.feature.order.domain.model.DishDto
import com.wishadish.feature.order.domain.model.Order
import com.wishadish.feature.order.domain.repository.OrderRepository
import com.wishadish.network.RetrofitClient

class RemoteOrderRepository : OrderRepository {
    private val api: OrderApi = RetrofitClient.createService(OrderApi::class.java)

    override suspend fun getDishes(): List<DishDto> {
        //delay(5000)
        //return mockDishes()
        return api.getDishes()
    }

    override suspend fun getDishesWithFavourites(token: String): List<DishDto> {
        return api.getDishesWithFavourites(token)
    }

    override suspend fun placeOrder(order: Order, token: String) {
        api.placeOrder(order, token)
    }

    override suspend fun addToFavourites(dish: Dish, token: String) {
        api.addToFavourites(dish.dishId, token)
    }

    override suspend fun removeFromFavourites(dish: Dish, token: String) {
        api.removeFromFavourites(dish.dishId, token)
    }

    fun mockDishes(): List<DishDto> {
        return listOf(
            DishDto(
                dishId = 1,
                name = "Margherita Pizza",
                category = "Pizza",
                price = 200.0,
                description = "Classic Margherita Pizza with fresh basil and mozzarella cheese.",
                imageUrl = "https://kristineskitchenblog.com/wp-content/uploads/2024/07/margherita-pizza-22-2.jpg"
            ),
            DishDto(
                dishId = 2,
                name = "Pepperoni Pizza",
                category = "Pizza",
                price = 250.0,
                description = "Spicy pepperoni with mozzarella cheese on a crispy crust.",
                imageUrl = "https://www.simplyrecipes.com/thmb/KE6iMblr3R2Db6oE8HdyVsFSj2A=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/__opt__aboutcom__coeus__resources__content_migration__simply_recipes__uploads__2019__09__easy-pepperoni-pizza-lead-3-1024x682-583b275444104ef189d693a64df625da.jpg"
            ),
            DishDto(
                dishId = 3,
                name = "Caesar Salad",
                category = "Salads",
                price = 150.0,
                description = "Fresh romaine lettuce with Caesar dressing, croutons, and parmesan.",
                imageUrl = "https://www.allrecipes.com/thmb/mXZ0Tulwn3x9_YB_ZbkiTveDYFE=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/229063-Classic-Restaurant-Caesar-Salad-ddmfs-4x3-231-89bafa5e54dd4a8c933cf2a5f9f12a6f.jpg"
            ),
            DishDto(
                dishId = 4,
                name = "Spaghetti Carbonara",
                category = "Pasta",
                price = 220.0,
                description = "Classic Italian pasta with creamy sauce, pancetta, and parmesan.",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSkgGno9XnyUFkDllTyJ14Y-tLjF5zvC79BkQ&s"
            ),
            DishDto(
                dishId = 5,
                name = "Chicken Alfredo",
                category = "Pasta",
                price = 240.0,
                description = "Creamy Alfredo sauce with grilled chicken and fettuccine pasta.",
                imageUrl = "https://bellyfull.net/wp-content/uploads/2021/02/Chicken-Alfredo-blog-3.jpg"
            ),
            DishDto(
                dishId = 6,
                name = "Greek Salad",
                category = "Salads",
                price = 160.0,
                description = "Crispy lettuce, tomatoes, cucumbers, olives, and feta cheese.",
                imageUrl = "https://i2.wp.com/www.downshiftology.com/wp-content/uploads/2018/08/Greek-Salad-main.jpg"
            ),
            DishDto(
                dishId = 7,
                name = "Chocolate Lava Cake",
                category = "Desserts",
                price = 180.0,
                description = "Rich chocolate cake with a gooey molten chocolate center.",
                imageUrl = "https://rhubarbandcod.com/wp-content/uploads/2022/02/Chocolate-Lava-Cakes-1.jpg"
            )
        )
    }
}