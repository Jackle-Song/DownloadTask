package com.jacklesong.downloadtask1.utils

interface ProgressListener {
    fun update(bytesRead: Long, contentLength: Long, done: Boolean)
}