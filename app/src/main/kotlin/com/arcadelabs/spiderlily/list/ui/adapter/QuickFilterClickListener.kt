package com.arcadelabs.spiderlily.list.ui.adapter

import com.arcadelabs.spiderlily.list.domain.ListFilterOption

interface QuickFilterClickListener {

	fun onFilterOptionClick(option: ListFilterOption)
}
