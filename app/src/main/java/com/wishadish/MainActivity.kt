package com.wishadish

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.wishadish.auth.data.GoogleAuthClient
import com.wishadish.auth.domain.model.User
import com.wishadish.auth.domain.repository.AuthRepository
import com.wishadish.auth.presentation.LoginScreen
import com.wishadish.auth.presentation.ProfileScreen
import com.wishadish.auth.presentation.SignUpScreen
import com.wishadish.auth.presentation.VerificationScreen
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
                NavHost(
                    navController = navController,
                    startDestination = if (!googleAuth.isSingedIn()) Screen.LoginScreen else Screen.ProfileScreen
                ) {
                    composable<Screen.LoginScreen> {

                        LoginScreen(
                            onLoginClick = { email: String,
                                             password: String ->
                                auth.signInWithEmailAndPassword(email.trim(), password.trim())
                                    .addOnSuccessListener {
                                        navController.navigate(Screen.ProfileScreen)
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
                                        navController.navigate(Screen.ProfileScreen)
                                    }
                                }
                            },
                            onForgotPasswordClick = { email: String ->
                                auth.sendPasswordResetEmail(email.trim())
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Toast.makeText(
                                                applicationContext,
                                                "Email is sent to $email",
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
                                    navController.navigate(Screen.ProfileScreen)
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
                                    navController.navigate(Screen.LoginScreen)
                                }
                            }
                        )
                    }
                    composable<Screen.VerificationScreen> {
                        val verificationScreen = it.toRoute<Screen.VerificationScreen>()

                        VerificationScreen(
                            email = verificationScreen.email,
                            onVerifiedClick = {
                                val user = auth.currentUser
                                if (user != null && user.isEmailVerified){
                                    navController.popBackStack()
                                    navController.navigate(Screen.ProfileScreen)
                                }
                                else {
                                    Toast.makeText(
                                        applicationContext,
                                        "Seems like your email is not verified",
                                        Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}