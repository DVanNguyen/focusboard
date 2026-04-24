package com.example.focusboard.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
)

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
)

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: Map<String, Any>? = null,
)

