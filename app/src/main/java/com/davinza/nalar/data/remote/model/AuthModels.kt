package com.davinza.nalar.data.remote.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val avatar_url: String?,
    val role: String,
    val points: Int,
    val rank_name: String?,
    val is_premium: Int,
    val premium_expires_at: String?,
    val keys_count: Int?,
    val streak_days: Int?,
    val total_correct: Int?,
    val total_answered: Int?,
    val created_at: String
)

data class AuthData(
    val token: String?,
    val user: User
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class GoogleLoginRequest(
    val idToken: String
)

data class UpdateProfileRequest(
    val name: String?,
    val avatar_url: String?
)

data class ChangePasswordRequest(
    val old_password: String,
    val new_password: String
)
