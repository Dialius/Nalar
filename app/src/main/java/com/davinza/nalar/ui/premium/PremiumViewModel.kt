package com.davinza.nalar.ui.premium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davinza.nalar.data.remote.model.PaymentData
import com.davinza.nalar.data.repository.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    data class Success(val paymentData: PaymentData) : PaymentState()
    data class Error(val message: String) : PaymentState()
}

sealed class PaymentPollingState {
    object Idle : PaymentPollingState()
    object Polling : PaymentPollingState()
    object Paid : PaymentPollingState()
    object Expired : PaymentPollingState()
    object Failed : PaymentPollingState()
}

class PremiumViewModel(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState

    private val _pollingState = MutableStateFlow<PaymentPollingState>(PaymentPollingState.Idle)
    val pollingState: StateFlow<PaymentPollingState> = _pollingState

    private var pollingJob: kotlinx.coroutines.Job? = null

    fun startPayment(paymentType: String, bank: String) {
        viewModelScope.launch {
            _paymentState.value = PaymentState.Loading
            
            val result = paymentRepository.createPayment(paymentType, bank)
            if (result.isSuccess) {
                val data = result.getOrNull()
                if (data != null) {
                    _paymentState.value = PaymentState.Success(data)
                } else {
                    _paymentState.value = PaymentState.Error("Data pembayaran tidak valid")
                }
            } else {
                _paymentState.value = PaymentState.Error(result.exceptionOrNull()?.message ?: "Gagal membuat pembayaran")
            }
        }
    }
    
    fun startPolling(orderId: String) {
        stopPolling()
        pollingJob = viewModelScope.launch {
            _pollingState.value = PaymentPollingState.Polling
            while (_pollingState.value is PaymentPollingState.Polling) {
                kotlinx.coroutines.delay(10000L)
                val result = paymentRepository.getPaymentStatus(orderId)
                result.onSuccess { status ->
                    val statusStr = status.status?.lowercase() ?: "pending"
                    when (statusStr) {
                        "success", "settlement", "capture" -> {
                            _pollingState.value = PaymentPollingState.Paid
                        }
                        "expire" -> {
                            _pollingState.value = PaymentPollingState.Expired
                        }
                        "cancel", "deny", "fraud" -> {
                            _pollingState.value = PaymentPollingState.Failed
                        }
                    }
                }
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
        if (_pollingState.value is PaymentPollingState.Polling) {
            _pollingState.value = PaymentPollingState.Idle
        }
    }

    fun resetState() {
        _paymentState.value = PaymentState.Idle
        _pollingState.value = PaymentPollingState.Idle
    }
}
