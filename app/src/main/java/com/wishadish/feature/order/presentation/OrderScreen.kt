package com.wishadish.feature.order.presentation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.wishadish.feature.order.domain.model.Dish

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    viewModel: OrderViewModel,
    onViewCartClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val filteredDishes = viewModel.displayedDishes
    val categories = viewModel.getAllCategories()
    val context = LocalContext.current

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                        Text(text = "Menu")
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Go to profile",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .padding(horizontal = 8.dp)) {
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                label = { Text("Search Dishes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                println("MAKING LAZYCOLUMN")
                println(categories)
                categories.forEach { category ->
                    val dishesInCategory = filteredDishes.filter { it.category == category }
                    if (dishesInCategory.isNotEmpty()) {
                        item { Text(text = category, style = androidx.compose.material3.MaterialTheme.typography.titleLarge) }
                        items(dishesInCategory) { dish ->
                            DishItem(dish = dish, onAddToCart = {
                                viewModel.addDishToCart(it)
                                Toast.makeText(
                                    context,
                                    "${it.name} is added to cart",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                        }
                    }
                }
            }

            Button(
                onClick = onViewCartClick,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
            ) {
                Text("View Cart")
            }
        }
    }
}

@Composable
fun DishItem(dish: Dish, onAddToCart: (Dish) -> Unit) {
    println("MAKING DISHITEM")
    println(IMAGE_LINK_PREFIX + dish.imageUrl)
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Image(
            painter = rememberAsyncImagePainter(IMAGE_LINK_PREFIX + dish.imageUrl),
            contentDescription = "${dish.name} image",
            modifier = Modifier
                .size(64.dp)
                .padding(end = 8.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = dish.name)
            Text(text = "%.2f UAH".format(dish.price))
        }
        Button(onClick = { onAddToCart(dish) }) {
            Text("Add to Cart")
        }
    }
}