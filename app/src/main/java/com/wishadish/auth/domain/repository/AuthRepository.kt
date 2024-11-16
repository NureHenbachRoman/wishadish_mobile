package com.wishadish.auth.domain.repository

import com.wishadish.auth.data.remote.AuthApiService
import com.wishadish.auth.domain.model.User
import com.wishadish.network.RetrofitClient

class AuthRepository {
    private val authApiService: AuthApiService by lazy {
        RetrofitClient.createService(AuthApiService::class.java)
    }

    suspend fun signUp(user: User) = authApiService.signUp(user)
}