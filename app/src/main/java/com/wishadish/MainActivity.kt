package com.wishadish

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.wishadish.feature.auth.data.GoogleAuthClient
import com.wishadish.feature.auth.domain.model.User
import com.wishadish.feature.auth.domain.repository.AuthRepository
import com.wishadish.feature.auth.presentation.LoginScreen
import com.wishadish.feature.auth.presentation.ProfileScreen
import com.wishadish.feature.auth.presentation.SignUpScreen
import com.wishadish.feature.order.data.remote.RemoteOrderRepository
import com.wishadish.feature.order.presentation.CartCheckoutScreen
import com.wishadish.feature.order.presentation.FavouritesScreen
import com.wishadish.feature.order.presentation.OrderScreen
import com.wishadish.feature.order.presentation.OrderViewModel
import com.wishadish.navigation.Screen
import com.wishadish.ui.theme.WishADishTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WishADishTheme {
                val navController = rememberNavController()
                val auth = Firebase.auth
                val googleAuth = GoogleAuthClient(context = this)
                val authRepository = AuthRepository()
                val orderViewModel = OrderViewModel(RemoteOrderRepository())
                NavHost(
                    navController = navController,
                    startDestination = Screen.OrderScreen
                ) {
                    composable<Screen.LoginScreen> {

                        LoginScreen(
                            onLoginClick = { email: String,
                                             password: String ->
                                auth.signInWithEmailAndPassword(email.trim(), password.trim())
                                    .addOnSuccessListener {
                                        navController.navigate(Screen.OrderScreen)
                                    }
                            },
                            onSignUpClick = {
                                navController.navigate(Screen.SignUpScreen)
                            },
                            onGoogleIconClick = {
                                lifecycleScope.launch {
                                    if (googleAuth.signIn()) {
                                        val user = auth.currentUser!!
                                        val fullName = user.displayName
                                        var firstName = ""
                                        var lastName = ""
                                        if (fullName != null) {
                                            val spaceIndex = fullName.indexOf(" ")
                                            if (spaceIndex == -1) {
                                                firstName = fullName
                                            } else {
                                                firstName = fullName.substring(0, spaceIndex)
                                                lastName = fullName.substring(spaceIndex)
                                            }
                                        }

                                        authRepository.signUp(
                                            User(
                                                token = user.getIdToken(false).await().token!!,
                                                firstName = firstName,
                                                lastName = lastName,
                                                phone = "",
                                                role = "client"
                                            )
                                        )
                                        navController.navigate(Screen.OrderScreen)
                                    }
                                }
                            },
                            onForgotPasswordClick = { email: String ->
                                auth.sendPasswordResetEmail(email.trim())
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Toast.makeText(
                                                applicationContext,
                                                "Email was sent to $email",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                applicationContext,
                                                "Failed to send email to $email",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                            }
                        )
                    }
                    composable<Screen.SignUpScreen> {
                        SignUpScreen(
                            onSignUpClick = { email: String,
                                              password: String,
                                              firstName: String,
                                              lastName: String,
                                              phone: String->
                                val reloadTask = auth.currentUser?.reload()
                                val user = auth.currentUser
                                if (user != null && user.isEmailVerified){
                                    lifecycleScope.launch {
                                        reloadTask?.await()
                                        authRepository.signUp(
                                            User(
                                                token = user.getIdToken(false).await().token!!,
                                                firstName = firstName,
                                                lastName = lastName,
                                                phone = phone,
                                                role = "client"
                                            )
                                        )
                                    }
                                    navController.navigate(Screen.OrderScreen)
                                }
                                else if (user == null) {
                                    auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                                        .addOnSuccessListener {
                                            val profileUpdates = UserProfileChangeRequest.Builder()
                                                .setDisplayName("$firstName $lastName")
                                                .build()
                                            auth.currentUser?.updateProfile(profileUpdates)
                                            auth.currentUser?.sendEmailVerification()
                                                ?.addOnSuccessListener {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Please verify your email (link sent to $email)",
                                                        Toast.LENGTH_LONG)
                                                        .show()
                                                }
                                        }
                                }
                                else if (!user.isEmailVerified) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Please verify your email",
                                        Toast.LENGTH_LONG)
                                        .show()
                                }
                            },
                            onLoginClick = {
                                navController.navigate(Screen.LoginScreen)
                            }
                        )
                    }
                    composable<Screen.ProfileScreen> {
                        ProfileScreen(
                            onSignOutClick = {
                                lifecycleScope.launch {
                                    googleAuth.signOut()
                                    Toast.makeText(
                                        applicationContext,
                                        "Signed out.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    navController.navigate(Screen.OrderScreen)
                                }
                            }
                        )
                    }
                    composable<Screen.OrderScreen> {
                        OrderScreen(
                            viewModel = orderViewModel,
                            onViewCartClick = {
                                navController.navigate(Screen.CartScreen)
                            },
                            onProfileClick = {
                                if (googleAuth.isSingedIn()) {
                                    navController.navigate(Screen.ProfileScreen)
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "You are not logged in",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate(Screen.LoginScreen)
                                }
                            },
                            onFavouritesClick = {
                                orderViewModel.updateSearchQuery("")
                                navController.navigate(Screen.FavouritesScreen)
                            },
                            onHistoryClick = {}
                        )
                    }
                    composable<Screen.CartScreen> {
                        CartCheckoutScreen(
                            viewModel = orderViewModel,
                            onPlaceOrderClick = {
                                if (!googleAuth.isSingedIn()) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Please login to place an order",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    navController.navigate(Screen.LoginScreen)
                                    return@CartCheckoutScreen false
                                }
                                return@CartCheckoutScreen true
                            },
                            onOrderPlaced = {
                                Toast.makeText(
                                    applicationContext,
                                    "Order Placed!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                orderViewModel.cartItems.clear()
                                navController.navigate(Screen.OrderScreen)
                            }
                        )
                    }
                    composable<Screen.FavouritesScreen> { 
                        FavouritesScreen(viewModel = orderViewModel)
                    }
                }
            }
        }
    }
}