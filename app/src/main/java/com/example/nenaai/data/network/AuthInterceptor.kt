package com.example.nenaai.data.network

import com.example.nenaai.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        val publicEndpoints = listOf(
            "register/",
            "verify-otp/",
            "login-with-pin/"
        )

        val isPublicEndpoint = publicEndpoints.any { originalRequest.url.encodedPath.contains(it) }

        // Only add Authorization header if it's not a public endpoint
        if (!isPublicEndpoint) {
            tokenManager.getToken()?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}