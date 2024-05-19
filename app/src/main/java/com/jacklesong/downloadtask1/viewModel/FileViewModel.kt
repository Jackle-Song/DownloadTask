package com.jacklesong.downloadtask1.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jacklesong.downloadtask1.model.DownloadDetailsVO
import com.jacklesong.downloadtask1.repository.FileRepository
import com.jacklesong.downloadtask1.utils.ProgressListener
import com.jacklesong.downloadtask1.utils.Utils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FileViewModel(private val repository: FileRepository) : ViewModel() {
    private val activeCalls = mutableMapOf<String, Call<ResponseBody>>()

    private val _downloadData = MutableLiveData<DownloadDetailsVO>()
    val downloadData: LiveData<DownloadDetailsVO> = _downloadData

    fun pauseDownload(fileUrl: String) {
        activeCalls[fileUrl]?.cancel()
        activeCalls.remove(fileUrl)
    }

    fun resumeDownload(fileUrl: String, outputPath: String, fileName: String, downloadData: DownloadDetailsVO) {
        downloadData.downloadLink = fileUrl
        val lastProgress = downloadData.progressStatus
        val totalBytes = downloadData.totalSize
        val startByte = (totalBytes?.times(lastProgress ?: 0) ?: 0L) / 100

        val progressListener = object : ProgressListener {
            override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                val additionalProgress = if (contentLength != -1L) {
                    (100 * bytesRead / contentLength).toInt()
                } else {
                    0
                }
                val totalProgress = lastProgress?.plus(additionalProgress)
                if (totalProgress != null) {
                    if (totalProgress % 20 == 0) {
                        downloadData.progressStatus = totalProgress
                        _downloadData.postValue(downloadData)
                    }
                }
            }
        }

        val call = repository.downloadFileFromByte(fileUrl, progressListener, startByte)
        activeCalls[fileUrl] = call
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        Utils.saveToFile(responseBody, outputPath, fileName)
                        downloadData.totalSize = responseBody.contentLength()
                        _downloadData.postValue(downloadData)
                    }
                } else {
                    println("Call Error : ${response.errorBody()}")
                }
                activeCalls.remove(fileUrl)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("Download failed: ${t.message}")
            }
        })
    }
}