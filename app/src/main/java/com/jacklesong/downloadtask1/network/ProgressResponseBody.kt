package com.jacklesong.downloadtask1.network

import com.jacklesong.downloadtask1.utils.ProgressListener
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.buffer

class ProgressResponseBody(
    private val responseBody: ResponseBody,
    private val progressListener: ProgressListener
) : ResponseBody() {

    private var bufferedSource: BufferedSource? = null

    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = responseBody.source().let {
                object : ForwardingSource(it) {
                    var totalBytesRead = 0L

                    override fun read(sink: Buffer, byteCount: Long): Long {
                        val bytesRead = super.read(sink, byteCount)
                        totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                        progressListener.update(totalBytesRead, contentLength(), bytesRead == -1L)
                        return bytesRead
                    }
                }.buffer()
            }
        }
        return bufferedSource!!
    }
}