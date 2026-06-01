package com.davinza.nalar.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davinza.nalar.AppEventBus
import com.davinza.nalar.data.remote.model.AuthData
import com.davinza.nalar.data.repository.GamificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val userProfile: AuthData) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel(
    private val gamificationRepository: GamificationRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState

    init {
        fetchProfile()
        // Subscribe to quiz completion events — refresh profile data automatically
        viewModelScope.launch {
            AppEventBus.events.collect { event ->
                when (event) {
                    is AppEventBus.AppEvent.QuizCompleted -> fetchProfile()
                }
            }
        }
    }

    fun fetchProfile() {
        viewModelScope.launch {
            // Keep existing data visible while refreshing (don't show Loading if we have data)
            val isRefresh = _profileState.value is ProfileState.Success
            if (!isRefresh) {
                _profileState.value = ProfileState.Loading
            }
            val result = gamificationRepository.getProfile()
            if (result.isSuccess) {
                val profileData = result.getOrNull()!!
                _profileState.value = ProfileState.Success(profileData)
                // Synchronize premium status, streak, and keys to UserProgressManager & automatically persist it to DataStore
                com.davinza.nalar.ui.courses.UserProgressManager.isPremium = (profileData.user.is_premium == 1)
                com.davinza.nalar.ui.courses.UserProgressManager.streakCount = profileData.user.streak_days ?: 0
                com.davinza.nalar.ui.courses.UserProgressManager.keysCount = profileData.user.keys_count ?: 2
            } else {
                // Only go to error state if we don't have any existing data
                if (_profileState.value !is ProfileState.Success) {
                    _profileState.value = ProfileState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }
}
