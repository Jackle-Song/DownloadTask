package com.jacklesong.downloadtask1.fragment

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.jacklesong.downloadtask1.R
import com.jacklesong.downloadtask1.adapter.DownloadListDetailsAdapter
import com.jacklesong.downloadtask1.databinding.FragmentDownloadDetailsBinding
import com.jacklesong.downloadtask1.model.DownloadDetailsVO
import com.jacklesong.downloadtask1.model.DownloadStatus
import com.jacklesong.downloadtask1.repository.FileRepository
import com.jacklesong.downloadtask1.repository.FileViewModelFactory
import com.jacklesong.downloadtask1.singleton.QueueSingletonObject
import com.jacklesong.downloadtask1.utils.Constants.DETAILS_FRAGMENT_TAB_NAME
import com.jacklesong.downloadtask1.viewModel.DownloadListViewModel
import com.jacklesong.downloadtask1.viewModel.FileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class SharedDetailsFragment : Fragment(), DownloadListDetailsAdapter.DownloadDetailsInterface {
    companion object {
        fun newInstance(tabName: String): SharedDetailsFragment {
            val fragment = SharedDetailsFragment()
            val args = Bundle()
            args.putString(DETAILS_FRAGMENT_TAB_NAME, tabName)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var binding: FragmentDownloadDetailsBinding
    private lateinit var fileViewModel: FileViewModel
    private lateinit var downloadListDetailsAdapter: DownloadListDetailsAdapter
    private lateinit var downloadListViewModel: DownloadListViewModel

    private var linkList : MutableList<String>? = mutableListOf()

    private val maxConcurrentDownloads = 3

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentDownloadDetailsBinding.inflate(inflater, container, false)

        downloadListViewModel = ViewModelProvider(requireActivity())[DownloadListViewModel::class.java]

        val repository = FileRepository()
        fileViewModel = ViewModelProvider(requireActivity(), FileViewModelFactory(repository))[FileViewModel::class.java]

        setupComponentListener()
        observeDownloadLiveData()
        initUI()

        return binding.root
    }

    override fun removeDownloadItem(downloadData: DownloadDetailsVO) {
        fileViewModel.pauseDownload(downloadData.downloadLink ?: "")
        downloadListViewModel.removeDownloadItem(downloadData)
    }

    override fun playPauseDownload(fileUrl: String) {
        val existingDownloadItem = downloadListViewModel.downloadDetailsList.value?.find { it.downloadLink == fileUrl }
        val index = downloadListViewModel.downloadDetailsList.value?.indexOf(existingDownloadItem)
        val viewHolder = binding.recyclerviewDownloadList.findViewHolderForAdapterPosition(index ?: 0) as? DownloadListDetailsAdapter.DownloadDetailsViewHolder

        if (existingDownloadItem?.isPaused == true) {
            val fileName = fileUrl.substringAfterLast("/").substringBeforeLast(".").substringBeforeLast(".")
            fileViewModel.resumeDownload(fileUrl, requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath.toString(), fileName, existingDownloadItem)
            existingDownloadItem.isPaused = false
            existingDownloadItem.status = DownloadStatus.DOWNLOADING
            QueueSingletonObject.addActiveDownloads(existingDownloadItem)
            viewHolder?.imgPlayPauseBtn?.setImageResource(R.drawable.ic_pause)
            viewHolder?.imgPlayPauseBtn?.tag = R.drawable.ic_pause

            if (QueueSingletonObject.getDownloadQueue().size > 0 && existingDownloadItem.id != QueueSingletonObject.getActiveDownloads()[0].id) {
                fileViewModel.pauseDownload(QueueSingletonObject.getActiveDownloads()[0].downloadLink ?: "")
                val activeDownloadItem = downloadListViewModel.downloadDetailsList.value?.find { it.id == QueueSingletonObject.getActiveDownloads()[0].id }
                val activeIndex = downloadListViewModel.downloadDetailsList.value?.indexOf(activeDownloadItem)
                val activeViewHolder = binding.recyclerviewDownloadList.findViewHolderForAdapterPosition(activeIndex ?: 0) as? DownloadListDetailsAdapter.DownloadDetailsViewHolder
                activeViewHolder?.imgPlayPauseBtn?.setImageResource(R.drawable.ic_play)
                activeViewHolder?.imgPlayPauseBtn?.tag = R.drawable.ic_play

                activeDownloadItem?.isPaused = true
                activeDownloadItem?.status = DownloadStatus.PAUSED
                if (activeDownloadItem != null) {
                    QueueSingletonObject.addDownloadQueue(activeDownloadItem)
                    downloadListViewModel.updateDownloadItem(activeDownloadItem)
                }
                QueueSingletonObject.removeDownloadQueue(existingDownloadItem)
            }
        }
        else {
            fileViewModel.pauseDownload(fileUrl)
            existingDownloadItem?.isPaused = true
            existingDownloadItem?.status = DownloadStatus.PAUSED
            viewHolder?.imgPlayPauseBtn?.setImageResource(R.drawable.ic_play)
            viewHolder?.imgPlayPauseBtn?.tag = R.drawable.ic_play
            if (existingDownloadItem != null) {
                QueueSingletonObject.addDownloadQueue(existingDownloadItem)
            }

            if (QueueSingletonObject.getDownloadQueue().isNotEmpty() && existingDownloadItem?.id != QueueSingletonObject.getDownloadQueue()[0].id) {
                val activeDownloadItem = downloadListViewModel.downloadDetailsList.value?.find { it.id == QueueSingletonObject.getDownloadQueue()[0].id }
                val activeIndex = downloadListViewModel.downloadDetailsList.value?.indexOf(activeDownloadItem)
                val activeViewHolder = binding.recyclerviewDownloadList.findViewHolderForAdapterPosition(activeIndex ?: 0) as? DownloadListDetailsAdapter.DownloadDetailsViewHolder
                activeViewHolder?.imgPlayPauseBtn?.setImageResource(R.drawable.ic_pause)
                activeViewHolder?.imgPlayPauseBtn?.tag = R.drawable.ic_pause

                activeDownloadItem?.isPaused = false
                if (activeDownloadItem != null) {
                    downloadListViewModel.updateDownloadItem(activeDownloadItem)
                }

                if (existingDownloadItem != null) {
                    manageQueue(existingDownloadItem)
                }
            }
        }

        if (existingDownloadItem != null) {
            downloadListViewModel.updateDownloadItem(existingDownloadItem)
        }
    }

    private fun initUI() {
        arguments?.let {
            binding.txtPageTitle.text = it.getString(DETAILS_FRAGMENT_TAB_NAME)
        }

        val inputStream = activity?.assets?.open("name.txt")
        inputStream?.bufferedReader()?.useLines { linkList?.addAll(it) }

        downloadListDetailsAdapter = DownloadListDetailsAdapter(requireContext(), downloadListViewModel.downloadDetailsList.value ?: mutableListOf(), this@SharedDetailsFragment)
        binding.recyclerviewDownloadList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerviewDownloadList.adapter = downloadListDetailsAdapter
        (binding.recyclerviewDownloadList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun setupComponentListener() {
        binding.imgAddDownload.setOnClickListener {
            val randomIndex = Random.nextInt(linkList?.size ?: 0)
            var randomItem = linkList?.get(randomIndex)

            while (randomItem != null && downloadListViewModel.downloadDetailsList.value?.any { it.downloadLink == randomItem } == true) {
                randomItem = linkList?.get(Random.nextInt(linkList?.size ?: 0))
            }

            if (randomItem != null) {
                val fileName = randomItem.substringAfterLast("/").substringBeforeLast(".").substringBeforeLast(".")
                downloadListViewModel.addDownloadItem(DownloadDetailsVO(downloadLink = randomItem, progressStatus = 0, downloadId = null, fileName = fileName))
                binding.recyclerviewDownloadList.scrollToPosition(0)
                val downloadData = downloadListViewModel.downloadDetailsList.value?.find { it.downloadLink == randomItem }
                if (downloadData != null) {
                    if (QueueSingletonObject.getActiveDownloads().size < maxConcurrentDownloads) {
                        getDownloadFile(randomItem, fileName, downloadData)
                    }
                    else {
                        QueueSingletonObject.addDownloadQueue(downloadData)
                    }
                }
            }
        }
    }

    private fun getDownloadFile(fileUrl: String, fileName: String, downloadData: DownloadDetailsVO) {
        CoroutineScope(Dispatchers.IO).launch {
            fileViewModel.resumeDownload(fileUrl, requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath.toString(), fileName, downloadData)
            QueueSingletonObject.addActiveDownloads(downloadData)
        }
    }

    private fun manageQueue(data : DownloadDetailsVO) {
        QueueSingletonObject.removeActiveDownloads(data)
        if (QueueSingletonObject.getActiveDownloads().size < maxConcurrentDownloads && QueueSingletonObject.getDownloadQueue().isNotEmpty()) {
            val existingDownloadItem = downloadListViewModel.downloadDetailsList.value?.find { it.id == QueueSingletonObject.getDownloadQueue()[0].id }
            if (existingDownloadItem != null) {
                getDownloadFile(existingDownloadItem.downloadLink ?: "", existingDownloadItem.fileName ?: "", existingDownloadItem)
            }
            QueueSingletonObject.removeDownloadQueueAtPosition(0)
        }
    }

    private fun observeDownloadLiveData() {
        downloadListViewModel.downloadDetailsList.observe(viewLifecycleOwner) { downloadDetailsList ->
            downloadListDetailsAdapter.updateDownloadDetailsList(downloadDetailsList)
        }

        fileViewModel.downloadData.observe(viewLifecycleOwner) { downloadData ->
            if (downloadData.downloaded != true) {
                CoroutineScope(Dispatchers.IO).launch {
                    val existingDownloadItem = downloadListViewModel.downloadDetailsList.value?.find { it.id == downloadData.id }
                    val index = downloadListViewModel.downloadDetailsList.value?.indexOf(existingDownloadItem)

                    withContext(Dispatchers.Main) {
                        val viewHolder = binding.recyclerviewDownloadList.findViewHolderForAdapterPosition(index ?: 0) as? DownloadListDetailsAdapter.DownloadDetailsViewHolder
                        viewHolder?.progressBarDownloadTask?.progress = downloadData.progressStatus ?: 0

                        if (viewHolder == null) {
                            downloadListDetailsAdapter.notifyItemChanged(index ?: 0)
                        }

                        if ((downloadData.progressStatus ?: 0) in 0 until  100 && !Integer.valueOf(R.drawable.ic_pause).equals( viewHolder?.imgPlayPauseBtn?.tag)) {
                            viewHolder?.imgPlayPauseBtn?.setImageResource(R.drawable.ic_pause)
                            viewHolder?.imgPlayPauseBtn?.tag = R.drawable.ic_pause
                            existingDownloadItem?.isPaused = false
                            existingDownloadItem?.status = DownloadStatus.DOWNLOADING
                        }
                        else if (downloadData.progressStatus == 100) {
                            val checkedActiveDownloadItem = QueueSingletonObject.getActiveDownloads().find { it.id == downloadData.id }
                            if (checkedActiveDownloadItem != null) {
                                manageQueue(checkedActiveDownloadItem)
                            }

                            viewHolder?.imgPlayPauseBtn?.visibility = View.INVISIBLE
                            existingDownloadItem?.downloaded = true
                            existingDownloadItem?.status = DownloadStatus.COMPLETED
                            if (existingDownloadItem != null) {
                                downloadListViewModel.updateDownloadItem(existingDownloadItem)
                            }
                        }
                    }
                }
            }
        }
    }

}