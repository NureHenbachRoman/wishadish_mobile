package com.wishadish.auth.data.remote

import com.wishadish.auth.domain.model.User
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/register")
    suspend fun signUp(@Body user: User)
}