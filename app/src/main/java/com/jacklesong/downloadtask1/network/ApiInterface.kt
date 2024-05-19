package com.jacklesong.downloadtask1.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Tag
import retrofit2.http.Url

interface ApiInterface {
    @GET
    fun downloadFile(@Url fileUrl: String, @Tag tag : String): Call<ResponseBody>

    @GET
    fun downloadFileFromByte(@Url fileUrl: String, @Header("Range") range: String): Call<ResponseBody>
}