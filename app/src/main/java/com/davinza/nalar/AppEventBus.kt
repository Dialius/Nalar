package com.davinza.nalar

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Global reactive event bus for broadcasting cross-screen state changes.
 * Used to trigger data refreshes on Profile and Leaderboard screens
 * immediately after a quiz session is completed and EXP is claimed.
 */
object AppEventBus {

    sealed class AppEvent {
        /** Emitted when a quiz session ends and EXP has been successfully claimed */
        data object QuizCompleted : AppEvent()
    }

    private val _events = MutableSharedFlow<AppEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    /** Emit a QuizCompleted event to trigger Profile & Leaderboard refresh */
    fun emitQuizCompleted() {
        _events.tryEmit(AppEvent.QuizCompleted)
    }
}
