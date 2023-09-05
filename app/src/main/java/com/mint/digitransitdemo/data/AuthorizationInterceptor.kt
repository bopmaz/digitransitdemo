package com.mint.digitransitdemo.data

import com.mint.digitransitdemo.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain
            .request()
            .newBuilder()
            .apply {
                addHeader("digitransit-subscription-key", BuildConfig.DIGITRANSIT_API_KEY)
            }
            .build()
        return chain.proceed(request)
    }
}