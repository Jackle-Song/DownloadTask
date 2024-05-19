package com.jacklesong.downloadtask1.utils

import okhttp3.ResponseBody
import java.io.File

object Utils {
    fun saveToFile(body: ResponseBody, outputPath: String, fileName: String) {
        val file = File(outputPath)
        if (!file.exists()) {
            file.mkdirs()
        }
        val filePath = File(outputPath, fileName)
        filePath.outputStream().use { fileOutputStream ->
            body.byteStream().use { inputStream ->
                inputStream.copyTo(fileOutputStream)
            }
        }
    }
}