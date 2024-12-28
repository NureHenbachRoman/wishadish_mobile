package com.wishadish.feature.order.presentation

import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun FavouritesScreen(
    viewModel: OrderViewModel,
) {
    val filteredDishes = viewModel.displayedDishes
    val categories = viewModel.getAllCategories()
    val context = LocalContext.current
    LazyColumn {
        println("MAKING LAZYCOLUMN")
        println(categories)
        categories.forEach { category ->
            val dishesInCategory = filteredDishes.filter { it.category == category }
            if (dishesInCategory.isNotEmpty()) {
                item { Text(text = category, style = MaterialTheme.typography.titleLarge) }
                items(dishesInCategory) { dish ->
                    if (dish.isFavourite.value == true) {
                        DishItem(
                            dish = dish,
                            onAddToCart = {
                                viewModel.addDishToCart(it)
                                Toast.makeText(
                                    context,
                                    "${it.name} is added to cart",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onFavouriteClick = {
                                viewModel.toggleFavourite(it)
                                Toast.makeText(
                                    context,
                                    "${it.name} is added to favourites",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }
        }
    }
}