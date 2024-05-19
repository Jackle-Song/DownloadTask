package com.jacklesong.downloadtask1.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jacklesong.downloadtask1.viewModel.FileViewModel

class FileViewModelFactory(private val repository: FileRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}