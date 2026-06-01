package com.davinza.nalar.data.remote

import android.content.Context
import com.davinza.nalar.data.local.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate

object ApiClient {
    private const val BASE_URL = "https://slateblue-moose-955883.hostingersite.com/" // URL Production Hostinger

    fun getApiService(context: Context): ApiService {
        val sessionManager = SessionManager(context)

        val authInterceptor = Interceptor { chain ->
            val token = runBlocking { sessionManager.getAuthToken().first() }
            val requestBuilder = chain.request().newBuilder()
            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }

        // Retry interceptor - otomatis coba ulang jika koneksi gagal (termasuk SSL handshake)
        val retryInterceptor = Interceptor { chain ->
            val request = chain.request()
            var response = runCatching { chain.proceed(request) }
            var tryCount = 0
            while (response.isFailure && tryCount < 3) {
                tryCount++
                Thread.sleep(500)
                response = runCatching { chain.proceed(request) }
            }
            response.getOrThrow()
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Trust manager yang menerima semua sertifikat SSL termasuk Hostinger
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL").apply {
            init(null, trustAllCerts, java.security.SecureRandom())
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(retryInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
