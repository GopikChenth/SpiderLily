package com.arcadelabs.spiderlily.explore.ui.adapter

import android.view.View
import com.arcadelabs.spiderlily.list.ui.adapter.ListHeaderClickListener
import com.arcadelabs.spiderlily.list.ui.adapter.ListStateHolderListener

interface ExploreListEventListener : ListStateHolderListener, View.OnClickListener, ListHeaderClickListener
