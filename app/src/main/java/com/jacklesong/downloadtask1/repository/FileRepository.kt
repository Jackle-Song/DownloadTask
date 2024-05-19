package com.jacklesong.downloadtask1.repository

import com.jacklesong.downloadtask1.network.ApiClient
import com.jacklesong.downloadtask1.utils.ProgressListener
import okhttp3.ResponseBody
import retrofit2.Call

class FileRepository {

    fun downloadFileFromByte(fileUrl: String, progressListener: ProgressListener, startByte : Long): Call<ResponseBody> {
        val service = ApiClient.retrofitService(progressListener, fileUrl)
        val rangeHeader = "bytes=$startByte-"
        return service.downloadFileFromByte(fileUrl, rangeHeader)
    }
}