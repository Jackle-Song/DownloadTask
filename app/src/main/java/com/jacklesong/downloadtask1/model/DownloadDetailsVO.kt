package com.jacklesong.downloadtask1.model

import java.util.UUID

data class DownloadDetailsVO(
    var id : String = UUID.randomUUID().toString(),
    var downloadLink : String? = null,
    var progressStatus : Int? = null,
    var downloaded : Boolean? = null,
    var downloadId : Int? = null,
    var isPaused : Boolean? = true,
    var totalSize : Long? = null,
    var outputPath : String? = null,
    var fileName : String? = null,
    var status: DownloadStatus = DownloadStatus.QUEUED
)

enum class DownloadStatus {
    QUEUED, DOWNLOADING, PAUSED, COMPLETED, FAILED
}