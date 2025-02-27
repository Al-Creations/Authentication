package com.example.authentication


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginApi {
    @POST("api/auth/signup")
    fun getSignup(@Body request:SignUpRequest): Call <MessageRespone>
}

data class MessageRespone (
    val message: String
)

data class SignUpRequest (
    val username: String,
    val password: String,
    val email: String,
    val role: Set<String>

)