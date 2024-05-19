package com.jacklesong.downloadtask1.network

import com.jacklesong.downloadtask1.utils.ProgressListener
import okhttp3.Interceptor
import okhttp3.Response

class ProgressInterceptor(private val progressListener: ProgressListener) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        return originalResponse.newBuilder()
            .body(ProgressResponseBody(originalResponse.body!!, progressListener))
            .build()
    }
}