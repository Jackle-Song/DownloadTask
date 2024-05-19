package com.jacklesong.downloadtask1.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jacklesong.downloadtask1.model.DownloadDetailsVO

class DownloadListViewModel : ViewModel() {
    private val _downloadDetailsList = MutableLiveData<MutableList<DownloadDetailsVO>>(mutableListOf())

    val downloadDetailsList: LiveData<MutableList<DownloadDetailsVO>>
        get() = _downloadDetailsList

    fun addDownloadItem(item: DownloadDetailsVO) {
        val currentList = _downloadDetailsList.value ?: mutableListOf()
        currentList.add(0, item)
        _downloadDetailsList.value = currentList
    }

    fun updateDownloadItem(updatedItem: DownloadDetailsVO) {
        val currentList = _downloadDetailsList.value
        val index = currentList?.indexOfFirst { it.id == updatedItem.id } ?: -1
        if (index != -1) {
            currentList?.set(index, updatedItem)
            _downloadDetailsList.value = currentList
        }
    }

    fun removeDownloadItem(item: DownloadDetailsVO) {
        val currentList = _downloadDetailsList.value
        if (currentList != null && currentList.size == 1) {
            clearDownloadList()
        }
        else {
            currentList?.remove(item)
            _downloadDetailsList.value = currentList
        }
    }

    private fun clearDownloadList() {
        _downloadDetailsList.value = mutableListOf()
    }
}