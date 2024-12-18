package com.wishadish.feature.order.presentation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.wishadish.feature.order.domain.model.CartItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun CartCheckoutScreen(viewModel: OrderViewModel, onOrderPlaced: () -> Unit) {
    val cartItems = viewModel.cartItems
    val totalPrice = cartItems.sumOf { it.dish.price * it.quantity }
    val context = LocalContext.current
    var pickupDateTime by remember { mutableStateOf(LocalDateTime.now()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(cartItems) { cartItem ->
                CartItemRow(
                    cartItem,
                    onIncreaseClick = { viewModel.increaseCartItemQuantity(it) },
                    onDecreaseClick = { viewModel.decreaseCartItemQuantity(it) },
                    onRemoveClick = { viewModel.removeDishFromCart(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Total Price: %.2f UAH".format(totalPrice),
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Pickup Time: ${pickupDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val now = LocalDateTime.now()
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    pickupDateTime = pickupDateTime.withYear(year).withMonth(month + 1).withDayOfMonth(dayOfMonth)
                },
                now.year, now.monthValue - 1, now.dayOfMonth
            ).show()
        }) {
            Text("Select Date")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val now = LocalDateTime.now()
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    pickupDateTime = pickupDateTime.withHour(hour).withMinute(minute)
                },
                now.hour, now.minute, true
            ).show()
        }) {
            Text("Select Time")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            Toast.makeText(context, "Order Placed!", Toast.LENGTH_SHORT).show()
            onOrderPlaced()
        }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Place Order")
        }
    }
}

@Composable
fun CartItemRow(
    cartItem: CartItem,
    onRemoveClick: (CartItem) -> Unit,
    onIncreaseClick: (CartItem) -> Unit,
    onDecreaseClick: (CartItem) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 0.dp)) {
        Image(
            painter = rememberAsyncImagePainter(cartItem.dish.imageUrl),
            contentDescription = "${cartItem.dish.name} image",
            modifier = Modifier.size(64.dp).padding(end = 8.dp)
        )
        Column(modifier = Modifier.weight(5f)) {
            Text(text = cartItem.dish.name)
            Text(text = "x${cartItem.quantity}")
            Text(text = "%.2f UAH".format(cartItem.dish.price * cartItem.quantity))
        }
        Button(
            onClick = { onDecreaseClick(cartItem) },
            enabled = cartItem.quantity > 1,
            modifier = Modifier.weight(2f)
        ) {
            Text("-")
        }
        Button(
            onClick = { onIncreaseClick(cartItem) },
            modifier = Modifier.weight(2f)
        ) {
            Text("+")
        }
        Button(onClick = { onRemoveClick(cartItem) }) {
            Text("Remove")
        }
    }
}