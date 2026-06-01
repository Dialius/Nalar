package com.davinza.nalar.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davinza.nalar.data.local.SessionManager
import com.davinza.nalar.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import com.davinza.nalar.data.remote.model.AuthData

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class UpdateProfileState {
    object Idle : UpdateProfileState()
    object Loading : UpdateProfileState()
    data class Success(val authData: AuthData) : UpdateProfileState()
    data class Error(val message: String) : UpdateProfileState()
}

sealed class ChangePasswordState {
    object Idle : ChangePasswordState()
    object Loading : ChangePasswordState()
    data class Success(val message: String) : ChangePasswordState()
    data class Error(val message: String) : ChangePasswordState()
}

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _updateProfileState = MutableStateFlow<UpdateProfileState>(UpdateProfileState.Idle)
    val updateProfileState: StateFlow<UpdateProfileState> = _updateProfileState

    private val _changePasswordState = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val changePasswordState: StateFlow<ChangePasswordState> = _changePasswordState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, password)
            if (result.isSuccess) {
                val data = result.getOrNull()
                if (data != null && data.token != null) {
                    sessionManager.saveAuthToken(data.token, data.user.id, data.user.is_premium)
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Invalid token")
                }
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.register(name, email, password)
            if (result.isSuccess) {
                val data = result.getOrNull()
                if (data != null && data.token != null) {
                    sessionManager.saveAuthToken(data.token, data.user.id, data.user.is_premium)
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Invalid token")
                }
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Register failed")
            }
        }
    }

    fun googleLogin(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.googleLogin(idToken)
            if (result.isSuccess) {
                val data = result.getOrNull()
                if (data != null && data.token != null) {
                    sessionManager.saveAuthToken(data.token, data.user.id, data.user.is_premium)
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Invalid token")
                }
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Google login failed")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _authState.value = AuthState.Idle
        }
    }

    fun updateProfile(name: String?, avatarUrl: String?) {
        viewModelScope.launch {
            _updateProfileState.value = UpdateProfileState.Loading
            val result = authRepository.updateProfile(name, avatarUrl)
            if (result.isSuccess) {
                val data = result.getOrNull()!!
                // Sync premium status to progress manager
                com.davinza.nalar.ui.courses.UserProgressManager.isPremium = (data.user.is_premium == 1)
                _updateProfileState.value = UpdateProfileState.Success(data)
            } else {
                _updateProfileState.value = UpdateProfileState.Error(result.exceptionOrNull()?.message ?: "Failed to update profile")
            }
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _changePasswordState.value = ChangePasswordState.Loading
            val result = authRepository.changePassword(oldPassword, newPassword)
            if (result.isSuccess) {
                _changePasswordState.value = ChangePasswordState.Success(result.getOrNull() ?: "Password changed successfully")
            } else {
                _changePasswordState.value = ChangePasswordState.Error(result.exceptionOrNull()?.message ?: "Failed to change password")
            }
        }
    }

    fun resetUpdateProfileState() {
        _updateProfileState.value = UpdateProfileState.Idle
    }

    fun resetChangePasswordState() {
        _changePasswordState.value = ChangePasswordState.Idle
    }
}
