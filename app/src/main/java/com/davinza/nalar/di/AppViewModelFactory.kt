package com.davinza.nalar.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.davinza.nalar.data.local.SessionManager
import com.davinza.nalar.data.remote.ApiClient
import com.davinza.nalar.data.repository.AuthRepository
import com.davinza.nalar.data.repository.CourseRepository
import com.davinza.nalar.data.repository.GamificationRepository
import com.davinza.nalar.data.repository.PaymentRepository
import com.davinza.nalar.ui.auth.AuthViewModel
import com.davinza.nalar.ui.home.HomeViewModel
import com.davinza.nalar.ui.premium.PremiumViewModel
import com.davinza.nalar.ui.profile.ProfileViewModel
import com.davinza.nalar.ui.leaderboard.LeaderboardViewModel

class AppViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val apiService = ApiClient.getApiService(context)
        val sessionManager = SessionManager(context)
        
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val repo = AuthRepository(apiService)
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repo, sessionManager) as T
        }
        
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val repo = GamificationRepository(apiService)
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repo) as T
        }

        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            val repo = GamificationRepository(apiService)
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repo) as T
        }

        if (modelClass.isAssignableFrom(PremiumViewModel::class.java)) {
            val repo = PaymentRepository(apiService)
            @Suppress("UNCHECKED_CAST")
            return PremiumViewModel(repo) as T
        }

        if (modelClass.isAssignableFrom(LeaderboardViewModel::class.java)) {
            val repo = GamificationRepository(apiService)
            @Suppress("UNCHECKED_CAST")
            return LeaderboardViewModel(repo) as T
        }
        
        // Add other viewmodels here later
        
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
