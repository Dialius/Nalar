package com.davinza.nalar.data.remote

import com.davinza.nalar.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<BaseResponse<AuthData>>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<BaseResponse<AuthData>>

    @POST("api/auth/google-login")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): Response<BaseResponse<AuthData>>

    @GET("api/auth/me")
    suspend fun getProfile(): Response<BaseResponse<AuthData>>

    @GET("api/courses")
    suspend fun getCourses(): Response<BaseResponse<List<Course>>>

    @GET("api/courses/{courseId}/modules")
    suspend fun getModules(@Path("courseId") courseId: Int): Response<BaseResponse<List<Module>>>

    @POST("api/payment/create")
    suspend fun createPayment(@Body request: PaymentRequest): Response<BaseResponse<PaymentData>>

    @GET("api/payment/status/{orderId}")
    suspend fun getPaymentStatus(@Path("orderId") orderId: String): Response<BaseResponse<TransactionStatusWrapper>>

    @GET("api/gamification/leaderboard")
    suspend fun getLeaderboard(): Response<BaseResponse<LeaderboardData>>

    @PUT("api/auth/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<BaseResponse<AuthData>>

    @PUT("api/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<BaseResponse<Any>>

    @POST("api/gamification/claim-exp")
    suspend fun claimExp(@Body request: ClaimExpRequest): Response<BaseResponse<ClaimExpData>>
}
