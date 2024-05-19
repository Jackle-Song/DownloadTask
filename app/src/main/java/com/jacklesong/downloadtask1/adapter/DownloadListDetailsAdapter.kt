package com.jacklesong.downloadtask1.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jacklesong.downloadtask1.R
import com.jacklesong.downloadtask1.model.DownloadDetailsVO
import com.jacklesong.downloadtask1.model.DownloadStatus
import com.jacklesong.downloadtask1.utils.DownloadDiffCallback
import java.io.File

class DownloadListDetailsAdapter(
    private val mContext : Context,
    private var downloadDetailsList : MutableList<DownloadDetailsVO>?,
    private val mListener : DownloadDetailsInterface,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val customPath = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath

    init {
        setHasStableIds(true)
    }

    interface DownloadDetailsInterface {
        fun removeDownloadItem(downloadData : DownloadDetailsVO)

        fun playPauseDownload(fileUrl: String)
    }

    class DownloadDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDownloadListLink : TextView = itemView.findViewById(R.id.txtDownloadListLink)
        val imgDeleteDownloadTask : ImageView = itemView.findViewById(R.id.imgDeleteDownloadTask)
        val imgPlayPauseBtn : ImageView = itemView.findViewById(R.id.imgPlayPauseBtn)
        val progressBarDownloadTask : ProgressBar = itemView.findViewById(R.id.progressBarDownloadTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_download_list_details, parent, false)
        return DownloadDetailsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return downloadDetailsList?.size ?: 0
    }

    override fun getItemId(position: Int): Long {
        return downloadDetailsList?.get(position)?.id.hashCode().toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val downloadDetailsItem = downloadDetailsList?.get(position)
        val downloadDetailsViewHolder = holder as DownloadDetailsViewHolder

        downloadDetailsViewHolder.txtDownloadListLink.text = downloadDetailsItem?.downloadLink
        downloadDetailsViewHolder.imgPlayPauseBtn.visibility = View.VISIBLE

        if (downloadDetailsItem?.downloaded == true) {
            downloadDetailsViewHolder.progressBarDownloadTask.progress = 100
            downloadDetailsViewHolder.imgPlayPauseBtn.visibility = View.INVISIBLE
        }
        else {
            downloadDetailsViewHolder.progressBarDownloadTask.progress = downloadDetailsItem?.progressStatus ?: 0
            when(downloadDetailsItem?.status) {
                DownloadStatus.DOWNLOADING -> {
                    downloadDetailsViewHolder.imgPlayPauseBtn.setImageResource(R.drawable.ic_pause)
                }
                DownloadStatus.PAUSED -> {
                    downloadDetailsViewHolder.imgPlayPauseBtn.setImageResource(R.drawable.ic_play)
                }
                else -> {
                    if (downloadDetailsViewHolder.imgPlayPauseBtn.tag != null) {
                        downloadDetailsViewHolder.imgPlayPauseBtn.setImageResource(downloadDetailsViewHolder.imgPlayPauseBtn.tag as Int)
                    }
                }
            }
        }

        downloadDetailsViewHolder.imgPlayPauseBtn.setOnClickListener {
            mListener.playPauseDownload(downloadDetailsItem?.downloadLink ?: "")
        }

        downloadDetailsViewHolder.imgDeleteDownloadTask.setOnClickListener {
            if (downloadDetailsItem?.progressStatus == 100) {
                val fileName = downloadDetailsItem.downloadLink?.substringAfterLast("/")?.substringBeforeLast(".")?.substringBeforeLast(".")
                val filePath = "$customPath/$fileName"
                val chosenFile = File(filePath)
                chosenFile.delete()
            }

            if (downloadDetailsItem != null) {
                mListener.removeDownloadItem(downloadDetailsItem)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDownloadDetailsList(newDownloadDetailsList: MutableList<DownloadDetailsVO>) {
        val diffCallback = DownloadDiffCallback(downloadDetailsList ?: mutableListOf(), newDownloadDetailsList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.downloadDetailsList = newDownloadDetailsList.toMutableList()
        if (downloadDetailsList?.size == 1) {
            notifyDataSetChanged()
        }
        diffResult.dispatchUpdatesTo(this)
    }
}