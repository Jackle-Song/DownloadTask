package com.jacklesong.downloadtask1.utils

import androidx.recyclerview.widget.DiffUtil
import com.jacklesong.downloadtask1.model.DownloadDetailsVO

class DownloadDiffCallback(
    private val oldList: MutableList<DownloadDetailsVO>,
    private val newList: MutableList<DownloadDetailsVO>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}