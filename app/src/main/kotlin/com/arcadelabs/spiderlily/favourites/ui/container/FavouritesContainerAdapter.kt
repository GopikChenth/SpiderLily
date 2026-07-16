package com.arcadelabs.spiderlily.favourites.ui.container

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.FlowCollector
import com.arcadelabs.spiderlily.core.util.ContinuationResumeRunnable
import com.arcadelabs.spiderlily.favourites.ui.list.FavouritesListFragment
import com.arcadelabs.spiderlily.list.ui.ListModelDiffCallback
import kotlin.coroutines.suspendCoroutine

class FavouritesContainerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment),
	FlowCollector<List<FavouriteTabModel>> {

	private val differ = AsyncListDiffer(
		AdapterListUpdateCallback(this),
		AsyncDifferConfig.Builder(ListModelDiffCallback<FavouriteTabModel>())
			.setBackgroundThreadExecutor(Dispatchers.IO.limitedParallelism(2).asExecutor())
			.build(),
	)

	override fun getItemCount(): Int = differ.currentList.size

	override fun getItemId(position: Int): Long {
		return differ.currentList.getOrNull(position)?.id ?: RecyclerView.NO_ID
	}

	override fun containsItem(itemId: Long): Boolean {
		return differ.currentList.any { x -> x.id == itemId }
	}

	override fun createFragment(position: Int): Fragment {
		val item = differ.currentList[position]
		return FavouritesListFragment.newInstance(item.id)
	}

	override suspend fun emit(value: List<FavouriteTabModel>) = suspendCoroutine { cont ->
		differ.submitList(value, ContinuationResumeRunnable(cont))
	}

	fun getItem(position: Int): FavouriteTabModel = differ.currentList[position]
}
