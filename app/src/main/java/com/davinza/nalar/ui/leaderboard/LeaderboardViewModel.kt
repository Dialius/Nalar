package com.davinza.nalar.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davinza.nalar.AppEventBus
import com.davinza.nalar.data.remote.model.AuthData
import com.davinza.nalar.data.remote.model.LeaderboardData
import com.davinza.nalar.data.repository.GamificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LeaderboardState {
    object Loading : LeaderboardState()
    data class Success(val leaderboard: LeaderboardData, val profile: AuthData) : LeaderboardState()
    data class Error(val message: String) : LeaderboardState()
}

class LeaderboardViewModel(
    private val gamificationRepository: GamificationRepository
) : ViewModel() {

    private val _state = MutableStateFlow<LeaderboardState>(LeaderboardState.Loading)
    val state: StateFlow<LeaderboardState> = _state

    init {
        loadData()
        // Subscribe to quiz completion events — refresh leaderboard automatically when EXP is claimed
        viewModelScope.launch {
            AppEventBus.events.collect { event ->
                when (event) {
                    is AppEventBus.AppEvent.QuizCompleted -> loadData(silent = true)
                }
            }
        }
    }

    fun loadData(silent: Boolean = false) {
        viewModelScope.launch {
            // Silent refresh: keep existing data visible while fetching fresh data
            if (!silent || _state.value !is LeaderboardState.Success) {
                _state.value = LeaderboardState.Loading
            }
            val leaderboardResult = gamificationRepository.getLeaderboard()
            val profileResult = gamificationRepository.getProfile()

            if (leaderboardResult.isSuccess && profileResult.isSuccess) {
                _state.value = LeaderboardState.Success(
                    leaderboard = leaderboardResult.getOrNull()!!,
                    profile = profileResult.getOrNull()!!
                )
            } else {
                // Only show error if we don't already have data displayed
                if (_state.value !is LeaderboardState.Success) {
                    val errorMsg = leaderboardResult.exceptionOrNull()?.message
                        ?: profileResult.exceptionOrNull()?.message
                        ?: "Failed to load leaderboard data"
                    _state.value = LeaderboardState.Error(errorMsg)
                }
            }
        }
    }
}
