package com.davinza.nalar.data.repository

import com.davinza.nalar.data.remote.ApiService
import com.davinza.nalar.data.remote.model.AuthData
import com.davinza.nalar.data.remote.model.LoginRequest

class AuthRepository(private val apiService: ApiService) {
    suspend fun login(email: String, password: String): Result<AuthData> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<AuthData> {
        return try {
            val response = apiService.register(com.davinza.nalar.data.remote.model.RegisterRequest(name, email, password))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Register failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun googleLogin(idToken: String): Result<AuthData> {
        return try {
            val response = apiService.googleLogin(com.davinza.nalar.data.remote.model.GoogleLoginRequest(idToken))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Google login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(name: String?, avatarUrl: String?): Result<AuthData> {
        return try {
            val response = apiService.updateProfile(com.davinza.nalar.data.remote.model.UpdateProfileRequest(name, avatarUrl))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to update profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): Result<String> {
        return try {
            val response = apiService.changePassword(com.davinza.nalar.data.remote.model.ChangePasswordRequest(oldPassword, newPassword))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.message ?: "Password changed successfully")
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to change password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
