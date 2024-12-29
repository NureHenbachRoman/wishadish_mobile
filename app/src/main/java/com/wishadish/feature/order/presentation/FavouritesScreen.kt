package com.wishadish.feature.order.presentation

import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun FavouritesScreen(
    viewModel: OrderViewModel,
) {
    val displayedDishes = viewModel.displayedDishes
    val context = LocalContext.current
    LazyColumn {
        items(displayedDishes) { dish ->
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
                            "${it.name} is removed from favourites",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }
}