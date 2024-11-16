package com.wishadish.auth.domain.model

data class User (
    val token: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val phone: String
)