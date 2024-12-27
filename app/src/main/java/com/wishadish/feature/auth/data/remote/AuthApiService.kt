package com.wishadish.feature.auth.data.remote

import com.wishadish.feature.auth.domain.model.User
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/register")
    suspend fun signUp(@Body user: User)
}