package com.davinza.nalar.data.remote.model

data class GamificationData(
    val points: Int,
    val rank_name: String,
    val is_premium: Int
)

data class LeaderboardEntry(
    val position: Int,
    val id: Int,
    val name: String,
    val avatar_url: String?,
    val points: Int,
    val rank_name: String?,
    val is_premium: Int
)

data class LeaderboardData(
    val leaderboard: List<LeaderboardEntry>,
    val my_position: Int
)

data class ClaimExpRequest(
    val activityType: String,
    val detail: String? = null,
    val correct: Int? = null,
    val total: Int? = null
)

data class ClaimExpData(
    val points_earned: Int,
    val total_points: Int,
    val rank_name: String
)
