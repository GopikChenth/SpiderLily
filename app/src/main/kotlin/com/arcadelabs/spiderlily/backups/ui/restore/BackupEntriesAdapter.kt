package com.arcadelabs.spiderlily.backups.ui.restore

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.arcadelabs.spiderlily.core.ui.BaseListAdapter
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.core.util.ext.setChecked
import com.arcadelabs.spiderlily.databinding.ItemCheckableMultipleBinding
import com.arcadelabs.spiderlily.list.ui.ListModelDiffCallback.Companion.PAYLOAD_CHECKED_CHANGED
import com.arcadelabs.spiderlily.list.ui.adapter.ListItemType

class BackupSectionsAdapter(
	clickListener: OnListItemClickListener<BackupSectionModel>,
) : BaseListAdapter<BackupSectionModel>() {

	init {
		addDelegate(ListItemType.NAV_ITEM, backupSectionAD(clickListener))
	}
}

private fun backupSectionAD(
	clickListener: OnListItemClickListener<BackupSectionModel>,
) = adapterDelegateViewBinding<BackupSectionModel, BackupSectionModel, ItemCheckableMultipleBinding>(
	{ layoutInflater, parent -> ItemCheckableMultipleBinding.inflate(layoutInflater, parent, false) },
) {

	binding.root.setOnClickListener { v ->
		clickListener.onItemClick(item, v)
	}

	bind { payloads ->
		with(binding.root) {
			setText(item.titleResId)
			setChecked(item.isChecked, PAYLOAD_CHECKED_CHANGED in payloads)
			isEnabled = item.isEnabled
		}
	}
}
