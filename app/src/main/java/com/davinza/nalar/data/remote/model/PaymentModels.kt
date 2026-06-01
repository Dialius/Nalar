package com.davinza.nalar.data.remote.model

data class PaymentRequest(
    val payment_type: String,
    val bank: String
)

data class VaNumber(
    val bank: String,
    val va_number: String
)

data class PaymentData(
    val order_id: String,
    val payment_type: String,
    val bank: String?,
    val va_numbers: List<VaNumber>?,
    val transaction_status: String
)

data class TransactionStatus(
    val id: Int,
    val order_id: String,
    val user_id: Int,
    val amount: String,
    val status: String?,
    val payment_type: String?,
    val created_at: String
)

data class TransactionStatusWrapper(
    val transaction: TransactionStatus
)
