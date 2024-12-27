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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.wishadish.feature.order.domain.model.CartItem
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CartCheckoutScreen(
    viewModel: OrderViewModel,
    onPlaceOrderClick: () -> Boolean,
    onOrderPlaced: () -> Unit
) {
    val cartItems = viewModel.cartItems
    val totalPrice = cartItems.sumOf { it.dish.price * it.quantity }
    val context = LocalContext.current
    var pickupDateTime by viewModel.pickupDateTime
    var customPickupTime by viewModel.customPickupTime

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)) {
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

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = customPickupTime,
                onCheckedChange = { customPickupTime = it }
            )
            Text("Custom Pickup Time")
        }

        if (customPickupTime) {
            Text(
                text = "Pickup Time: ${pickupDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                val nowInstant = Instant.now()
                val nowZonedDateTime = nowInstant.atZone(ZoneId.systemDefault())
                val endOfDay = nowZonedDateTime.toLocalDate().atTime(23, 59).atZone(ZoneId.systemDefault())

                val remainingMinutes = Duration.between(nowZonedDateTime, endOfDay).toMinutes()

                val minDate = if (remainingMinutes > 20) {
                    nowZonedDateTime.toLocalDate()
                } else {
                    nowZonedDateTime.toLocalDate().plusDays(1)
                }

                val datePickerDialog = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        pickupDateTime = pickupDateTime.withYear(year).withMonth(month + 1).withDayOfMonth(dayOfMonth)
                    },
                    nowZonedDateTime.year, nowZonedDateTime.monthValue - 1, nowZonedDateTime.dayOfMonth
                )

                datePickerDialog.datePicker.minDate = minDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val maxDate = minDate.plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                datePickerDialog.datePicker.maxDate = maxDate

                datePickerDialog.show()
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
        }

        Button(
            onClick = {
                if (!onPlaceOrderClick()) {
                    return@Button
                }

                if (customPickupTime && pickupDateTime.isBefore(LocalDateTime.now())){
                    Toast.makeText(
                        context,
                        "Time must be after now",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    viewModel.placeOrder(onSuccess = onOrderPlaced)
                }
                onOrderPlaced()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
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
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp, horizontal = 0.dp)) {
        Image(
            painter = rememberAsyncImagePainter(IMAGE_LINK_PREFIX + cartItem.dish.imageUrl),
            contentDescription = "${cartItem.dish.name} image",
            modifier = Modifier
                .size(64.dp)
                .padding(end = 8.dp)
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