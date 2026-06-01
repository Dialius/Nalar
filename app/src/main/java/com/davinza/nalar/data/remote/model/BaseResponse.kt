package com.davinza.nalar.data.remote.model

data class BaseResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)
