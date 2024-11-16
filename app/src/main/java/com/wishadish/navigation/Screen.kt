package com.wishadish.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object LoginScreen

    @Serializable
    data object SignUpScreen

    @Serializable
    data object ProfileScreen

    @Serializable
    data class VerificationScreen(val email: String)
}