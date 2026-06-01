package com.davinza.nalar.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davinza.nalar.AppEventBus
import com.davinza.nalar.data.remote.model.AuthData
import com.davinza.nalar.data.repository.GamificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val userProfile: AuthData) : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeViewModel(
    private val gamificationRepository: GamificationRepository
) : ViewModel() {

    private val _homeState = MutableStateFlow<HomeState>(HomeState.Loading)
    val homeState: StateFlow<HomeState> = _homeState

    init {
        fetchProfile()
        // Subscribe to quiz completion events — refresh home stats automatically when EXP is claimed
        viewModelScope.launch {
            AppEventBus.events.collect { event ->
                when (event) {
                    is AppEventBus.AppEvent.QuizCompleted -> fetchProfile(silent = true)
                }
            }
        }
    }

    fun fetchProfile(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent || _homeState.value !is HomeState.Success) {
                _homeState.value = HomeState.Loading
            }
            val result = gamificationRepository.getProfile()
            if (result.isSuccess) {
                val profileData = result.getOrNull()!!
                _homeState.value = HomeState.Success(profileData)
                // Synchronize premium status, streak, and keys to UserProgressManager & automatically persist it to DataStore
                com.davinza.nalar.ui.courses.UserProgressManager.isPremium = (profileData.user.is_premium == 1)
                com.davinza.nalar.ui.courses.UserProgressManager.streakCount = profileData.user.streak_days ?: 0
                com.davinza.nalar.ui.courses.UserProgressManager.keysCount = profileData.user.keys_count ?: 2
            } else {
                if (_homeState.value !is HomeState.Success) {
                    _homeState.value = HomeState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }
}
