package com.davinza.nalar.data.repository

import com.davinza.nalar.data.remote.ApiService
import com.davinza.nalar.data.remote.model.PaymentData

import com.davinza.nalar.data.remote.model.PaymentRequest
import com.davinza.nalar.data.remote.model.TransactionStatus

class PaymentRepository(private val apiService: ApiService) {
    suspend fun createPayment(paymentType: String, bank: String): Result<PaymentData> {
        return try {
            val response = apiService.createPayment(PaymentRequest(paymentType, bank))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to create payment"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPaymentStatus(orderId: String): Result<TransactionStatus> {
        return try {
            val response = apiService.getPaymentStatus(orderId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!.transaction)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal mendapatkan status pembayaran"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
