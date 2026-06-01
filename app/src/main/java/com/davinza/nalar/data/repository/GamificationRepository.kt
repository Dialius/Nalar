package com.davinza.nalar.data.repository

import com.davinza.nalar.data.remote.ApiService
import com.davinza.nalar.data.remote.model.AuthData
import com.davinza.nalar.data.remote.model.LeaderboardData

class GamificationRepository(private val apiService: ApiService) {
    suspend fun getProfile(): Result<AuthData> {
        return try {
            val response = apiService.getProfile()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Failed to fetch profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLeaderboard(): Result<LeaderboardData> {
        return try {
            val response = apiService.getLeaderboard()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Failed to fetch leaderboard"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun claimExp(activityType: String, detail: String? = null): Result<com.davinza.nalar.data.remote.model.ClaimExpData> {
        return try {
            val response = apiService.claimExp(com.davinza.nalar.data.remote.model.ClaimExpRequest(activityType, detail))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to claim EXP"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
