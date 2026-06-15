package com.arcadelabs.spiderlily.picker.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import com.arcadelabs.spiderlily.core.ui.BaseViewModel
import com.arcadelabs.spiderlily.core.util.ext.MutableEventFlow
import com.arcadelabs.spiderlily.core.util.ext.call
import com.arcadelabs.spiderlily.reader.ui.PageSaveHelper
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PageImagePickViewModel @Inject constructor() : BaseViewModel() {

	val onFileReady = MutableEventFlow<File>()

	fun savePageToTempFile(pageSaveHelper: PageSaveHelper, task: PageSaveHelper.Task) {
		launchLoadingJob(Dispatchers.IO) {
			val file = pageSaveHelper.saveToTempFile(task)
			onFileReady.call(file)
		}
	}
}
