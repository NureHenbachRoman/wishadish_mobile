package com.wishadish.feature.auth.domain.repository

import com.wishadish.feature.auth.data.remote.AuthApiService
import com.wishadish.feature.auth.domain.model.User
import com.wishadish.network.RetrofitClient

class AuthRepository {
    private val authApiService: AuthApiService by lazy {
        RetrofitClient.createService(AuthApiService::class.java)
    }

    suspend fun signUp(user: User) = authApiService.signUp(user)
}