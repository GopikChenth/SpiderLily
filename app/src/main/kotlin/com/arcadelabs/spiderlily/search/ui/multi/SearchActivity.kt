package com.arcadelabs.spiderlily.search.ui.multi

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.view.ActionMode
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import dagger.hilt.android.AndroidEntryPoint
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.exceptions.resolve.SnackbarErrorObserver
import com.arcadelabs.spiderlily.core.nav.router
import com.arcadelabs.spiderlily.core.prefs.AppSettings
import com.arcadelabs.spiderlily.core.ui.BaseActivity
import com.arcadelabs.spiderlily.core.ui.list.ListSelectionController
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.core.ui.widgets.TipView
import com.arcadelabs.spiderlily.core.util.ShareHelper
import com.arcadelabs.spiderlily.core.util.ext.consumeAllSystemBarsInsets
import com.arcadelabs.spiderlily.core.util.ext.invalidateNestedItemDecorations
import com.arcadelabs.spiderlily.core.util.ext.observe
import com.arcadelabs.spiderlily.core.util.ext.observeEvent
import com.arcadelabs.spiderlily.core.util.ext.systemBarsInsets
import com.arcadelabs.spiderlily.databinding.ActivitySearchBinding
import com.arcadelabs.spiderlily.list.domain.ListFilterOption
import com.arcadelabs.spiderlily.list.ui.MangaSelectionDecoration
import com.arcadelabs.spiderlily.list.ui.adapter.MangaListListener
import com.arcadelabs.spiderlily.list.ui.adapter.TypedListSpacingDecoration
import com.arcadelabs.spiderlily.list.ui.model.ListHeader
import com.arcadelabs.spiderlily.list.ui.model.MangaListModel
import com.arcadelabs.spiderlily.list.ui.size.DynamicItemSizeResolver
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaTag
import com.arcadelabs.spiderlily.search.domain.SearchKind
import com.arcadelabs.spiderlily.search.ui.multi.adapter.SearchAdapter
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity :
	BaseActivity<ActivitySearchBinding>(),
	MangaListListener,
	ListSelectionController.Callback {

	@Inject
	lateinit var settings: AppSettings

	private val viewModel by viewModels<SearchViewModel>()
	private lateinit var selectionController: ListSelectionController

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(ActivitySearchBinding.inflate(layoutInflater))
		title = when (viewModel.kind) {
			SearchKind.SIMPLE,
			SearchKind.TITLE -> viewModel.query

			SearchKind.AUTHOR -> getString(
				R.string.inline_preference_pattern,
				getString(R.string.author),
				viewModel.query,
			)

			SearchKind.TAG -> getString(R.string.inline_preference_pattern, getString(R.string.genre), viewModel.query)
		}

		val itemClickListener = OnListItemClickListener<SearchResultsListModel> { item, view ->
			if (item.listFilter == null) {
				router.openSearch(item.source, viewModel.query)
			} else {
				router.openList(item.source, item.listFilter, item.sortOrder)
			}
		}
		val sizeResolver = DynamicItemSizeResolver(resources, this, settings, adjustWidth = true)
		val selectionDecoration = MangaSelectionDecoration(this)
		selectionController = ListSelectionController(
			appCompatDelegate = delegate,
			decoration = selectionDecoration,
			registryOwner = this,
			callback = this,
		)
		val adapter = SearchAdapter(
			listener = this,
			itemClickListener = itemClickListener,
			sizeResolver = sizeResolver,
			selectionDecoration = selectionDecoration,
		)
		viewBinding.recyclerView.adapter = adapter
		viewBinding.recyclerView.setHasFixedSize(true)
		viewBinding.recyclerView.addItemDecoration(TypedListSpacingDecoration(this, true))

		setDisplayHomeAsUp(isEnabled = true, showUpAsClose = false)
		supportActionBar?.setSubtitle(R.string.search_results)

		addMenuProvider(SearchMenuProvider(this, viewModel))

		viewModel.list.observe(this, adapter)
		viewModel.onError.observeEvent(this, SnackbarErrorObserver(viewBinding.recyclerView, null))
	}

	override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
		val barsInsets = insets.systemBarsInsets
		viewBinding.toolbar.updatePadding(
			top = barsInsets.top,
			left = barsInsets.left,
			right = barsInsets.right,
		)
		viewBinding.recyclerView.setPadding(
			left = barsInsets.left,
			top = 0,
			right = barsInsets.right,
			bottom = barsInsets.bottom,
		)
		return insets.consumeAllSystemBarsInsets()
	}

	override fun onItemClick(item: MangaListModel, view: View) {
		if (!selectionController.onItemClick(item.id)) {
			router.openDetails(item.toMangaWithOverride())
		}
	}

	override fun onItemLongClick(item: MangaListModel, view: View): Boolean {
		return selectionController.onItemLongClick(view, item.id)
	}

	override fun onItemContextClick(item: MangaListModel, view: View): Boolean {
		return selectionController.onItemContextClick(view, item.id)
	}

	override fun onReadClick(manga: Manga, view: View) {
		if (!selectionController.onItemClick(manga.id)) {
			router.openReader(manga)
		}
	}

	override fun onTagClick(manga: Manga, tag: MangaTag, view: View) {
		if (!selectionController.onItemClick(manga.id)) {
			router.openList(tag)
		}
	}

	override fun onRetryClick(error: Throwable) {
		viewModel.retry()
	}

	override fun onFilterOptionClick(option: ListFilterOption) = Unit

	override fun onFilterClick(view: View?) = Unit

	override fun onEmptyActionClick() = viewModel.continueSearch()

	override fun onListHeaderClick(item: ListHeader, view: View) = Unit

	override fun onFooterButtonClick() = viewModel.continueSearch()

	override fun onPrimaryButtonClick(tipView: TipView) = Unit

	override fun onSecondaryButtonClick(tipView: TipView) = Unit

	override fun onSelectionChanged(controller: ListSelectionController, count: Int) {
		viewBinding.recyclerView.invalidateNestedItemDecorations()
	}

	override fun onCreateActionMode(
		controller: ListSelectionController,
		menuInflater: MenuInflater,
		menu: Menu
	): Boolean {
		menuInflater.inflate(R.menu.mode_remote, menu)
		return true
	}

	override fun onActionItemClicked(controller: ListSelectionController, mode: ActionMode?, item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.action_share -> {
				ShareHelper(this).shareMangaLinks(collectSelectedItems())
				mode?.finish()
				true
			}

			R.id.action_favourite -> {
				router.showFavoriteDialog(collectSelectedItems())
				mode?.finish()
				true
			}

			R.id.action_save -> {
				router.showDownloadDialog(collectSelectedItems(), viewBinding.recyclerView)
				mode?.finish()
				true
			}

			else -> false
		}
	}

	private fun collectSelectedItems(): Set<Manga> {
		return viewModel.getItems(selectionController.peekCheckedIds())
	}
}
