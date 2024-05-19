package com.jacklesong.downloadtask1.network

import com.jacklesong.downloadtask1.utils.Constants
import com.jacklesong.downloadtask1.utils.ProgressListener
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    fun retrofitService(progressListener: ProgressListener, tag: String): ApiInterface {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(execOkHttpClient(progressListener, tag))
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiInterface::class.java)
    }

    private fun execOkHttpClient(progressListener: ProgressListener?, tag: String): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .apply {
                if (progressListener != null) {
                    addNetworkInterceptor(ProgressInterceptor(progressListener))
                }
            }
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder().tag(tag).build()
                chain.proceed(request)
            }
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .build()
    }
}