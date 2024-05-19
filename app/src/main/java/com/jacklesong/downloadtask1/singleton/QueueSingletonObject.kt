package com.jacklesong.downloadtask1.singleton

import com.jacklesong.downloadtask1.model.DownloadDetailsVO

object QueueSingletonObject {
    private val downloadQueue = mutableListOf<DownloadDetailsVO>()
    private val activeDownloads = mutableListOf<DownloadDetailsVO>()

    fun addDownloadQueue(item: DownloadDetailsVO) {
        downloadQueue.add(item)
    }

    fun removeDownloadQueue(item: DownloadDetailsVO) {
        downloadQueue.remove(item)
    }

    fun removeDownloadQueueAtPosition(position: Int) {
        downloadQueue.removeAt(position)
    }

    fun getDownloadQueue() : MutableList<DownloadDetailsVO> {
        return downloadQueue
    }

    fun addActiveDownloads(item: DownloadDetailsVO) {
        activeDownloads.add(item)
    }

    fun removeActiveDownloads(item: DownloadDetailsVO) {
        activeDownloads.remove(item)
    }

    fun getActiveDownloads() : MutableList<DownloadDetailsVO> {
        return activeDownloads
    }
}