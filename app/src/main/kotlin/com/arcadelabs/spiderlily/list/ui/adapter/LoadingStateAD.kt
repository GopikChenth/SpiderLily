package com.arcadelabs.spiderlily.list.ui.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegate
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.list.ui.model.LoadingState

fun loadingStateAD() = adapterDelegate<LoadingState, ListModel>(R.layout.item_loading_state) {
}