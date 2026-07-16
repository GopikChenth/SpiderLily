package com.arcadelabs.spiderlily.details.ui.pager.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ActionMode
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.bookmarks.domain.Bookmark
import com.arcadelabs.spiderlily.bookmarks.ui.BookmarksSelectionDecoration
import com.arcadelabs.spiderlily.bookmarks.ui.adapter.BookmarksAdapter
import com.arcadelabs.spiderlily.core.exceptions.resolve.SnackbarErrorObserver
import com.arcadelabs.spiderlily.core.nav.ReaderIntent
import com.arcadelabs.spiderlily.core.nav.dismissParentDialog
import com.arcadelabs.spiderlily.core.nav.router
import com.arcadelabs.spiderlily.core.prefs.AppSettings
import com.arcadelabs.spiderlily.core.ui.BaseFragment
import com.arcadelabs.spiderlily.core.ui.list.ListSelectionController
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.core.ui.util.PagerNestedScrollHelper
import com.arcadelabs.spiderlily.core.ui.util.RecyclerViewOwner
import com.arcadelabs.spiderlily.core.ui.util.ReversibleActionObserver
import com.arcadelabs.spiderlily.core.util.ext.consumeAllSystemBarsInsets
import com.arcadelabs.spiderlily.core.util.ext.findAppCompatDelegate
import com.arcadelabs.spiderlily.core.util.ext.findParentCallback
import com.arcadelabs.spiderlily.core.util.ext.observe
import com.arcadelabs.spiderlily.core.util.ext.observeEvent
import com.arcadelabs.spiderlily.core.util.ext.systemBarsInsets
import com.arcadelabs.spiderlily.databinding.FragmentMangaBookmarksBinding
import com.arcadelabs.spiderlily.details.ui.pager.ChaptersPagesViewModel
import com.arcadelabs.spiderlily.list.ui.GridSpanResolver
import com.arcadelabs.spiderlily.list.ui.adapter.ListItemType
import com.arcadelabs.spiderlily.list.ui.adapter.TypedListSpacingDecoration
import com.arcadelabs.spiderlily.reader.ui.PageSaveHelper
import com.arcadelabs.spiderlily.reader.ui.ReaderNavigationCallback
import javax.inject.Inject

@AndroidEntryPoint
class BookmarksFragment : BaseFragment<FragmentMangaBookmarksBinding>(),
	OnListItemClickListener<Bookmark>,
	RecyclerViewOwner,
	ListSelectionController.Callback {

	private val activityViewModel by ChaptersPagesViewModel.ActivityVMLazy(this)
	private val viewModel by viewModels<BookmarksViewModel>()

	@Inject
	lateinit var settings: AppSettings

	@Inject
	lateinit var pageSaveHelperFactory: PageSaveHelper.Factory

	override val recyclerView: RecyclerView?
		get() = viewBinding?.recyclerView

	private lateinit var pageSaveHelper: PageSaveHelper
	private var bookmarksAdapter: BookmarksAdapter? = null
	private var spanResolver: GridSpanResolver? = null
	private var selectionController: ListSelectionController? = null

	private val spanSizeLookup = SpanSizeLookup()
	private val listCommitCallback = Runnable {
		spanSizeLookup.invalidateCache()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		pageSaveHelper = pageSaveHelperFactory.create(this)
		activityViewModel.mangaDetails.observe(this, viewModel)
	}

	override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMangaBookmarksBinding {
		return FragmentMangaBookmarksBinding.inflate(inflater, container, false)
	}

	override fun onViewBindingCreated(binding: FragmentMangaBookmarksBinding, savedInstanceState: Bundle?) {
		super.onViewBindingCreated(binding, savedInstanceState)
		spanResolver = GridSpanResolver(binding.root.resources)
		selectionController = ListSelectionController(
			appCompatDelegate = checkNotNull(findAppCompatDelegate()),
			decoration = BookmarksSelectionDecoration(binding.root.context),
			registryOwner = this,
			callback = this,
		)
		bookmarksAdapter = BookmarksAdapter(
			clickListener = this@BookmarksFragment,
			headerClickListener = null,
		)
		viewModel.gridScale.observe(viewLifecycleOwner, ::onGridScaleChanged) // before rv initialization
		with(binding.recyclerView) {
			addItemDecoration(TypedListSpacingDecoration(context, false))
			setHasFixedSize(true)
			PagerNestedScrollHelper(this).bind(viewLifecycleOwner)
			adapter = bookmarksAdapter
			addOnLayoutChangeListener(spanResolver)
			(layoutManager as GridLayoutManager).let {
				it.spanSizeLookup = spanSizeLookup
				it.spanCount = checkNotNull(spanResolver).spanCount
			}
			selectionController?.attachToRecyclerView(this)
		}
		viewModel.content.observe(viewLifecycleOwner) { bookmarksAdapter?.setItems(it, listCommitCallback) }

		viewModel.onError.observeEvent(
			viewLifecycleOwner,
			SnackbarErrorObserver(binding.recyclerView, this),
		)
		viewModel.onActionDone.observeEvent(viewLifecycleOwner, ReversibleActionObserver(binding.recyclerView))
	}

	override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
		val barsInsets = insets.systemBarsInsets
		viewBinding?.recyclerView?.setPadding(
			barsInsets.left,
			barsInsets.top,
			barsInsets.right,
			barsInsets.bottom,
		)
		return insets.consumeAllSystemBarsInsets()
	}

	override fun onDestroyView() {
		spanResolver = null
		bookmarksAdapter = null
		selectionController = null
		spanSizeLookup.invalidateCache()
		super.onDestroyView()
	}

	override fun onItemClick(item: Bookmark, view: View) {
		if (selectionController?.onItemClick(item.pageId) == true) {
			return
		}
		val listener = findParentCallback(ReaderNavigationCallback::class.java)
		if (listener != null && listener.onBookmarkSelected(item)) {
			dismissParentDialog()
		} else {
			val intent = ReaderIntent.Builder(view.context)
				.manga(activityViewModel.getMangaOrNull() ?: return)
				.bookmark(item)
				.incognito()
				.build()
			router.openReader(intent)
		}
	}

	override fun onItemLongClick(item: Bookmark, view: View): Boolean {
		return selectionController?.onItemLongClick(view, item.pageId) == true
	}

	override fun onItemContextClick(item: Bookmark, view: View): Boolean {
		return selectionController?.onItemContextClick(view, item.pageId) == true
	}

	override fun onSelectionChanged(controller: ListSelectionController, count: Int) {
		requireViewBinding().recyclerView.invalidateItemDecorations()
	}

	override fun onCreateActionMode(
		controller: ListSelectionController,
		menuInflater: MenuInflater,
		menu: Menu,
	): Boolean {
		menuInflater.inflate(R.menu.mode_bookmarks, menu)
		return true
	}

	override fun onActionItemClicked(
		controller: ListSelectionController,
		mode: ActionMode?,
		item: MenuItem,
	): Boolean {
		return when (item.itemId) {
			R.id.action_remove -> {
				val ids = selectionController?.snapshot() ?: return false
				viewModel.removeBookmarks(ids)
				mode?.finish()
				true
			}

			R.id.action_save -> {
				viewModel.savePages(pageSaveHelper, selectionController?.snapshot() ?: return false)
				mode?.finish()
				true
			}

			else -> false
		}
	}

	private fun onGridScaleChanged(scale: Float) {
		spanSizeLookup.invalidateCache()
		spanResolver?.setGridSize(scale, requireViewBinding().recyclerView)
	}

	private inner class SpanSizeLookup : GridLayoutManager.SpanSizeLookup() {

		init {
			isSpanIndexCacheEnabled = true
			isSpanGroupIndexCacheEnabled = true
		}

		override fun getSpanSize(position: Int): Int {
			val total = (viewBinding?.recyclerView?.layoutManager as? GridLayoutManager)?.spanCount ?: return 1
			return when (bookmarksAdapter?.getItemViewType(position)) {
				ListItemType.PAGE_THUMB.ordinal -> 1
				else -> total
			}
		}

		fun invalidateCache() {
			invalidateSpanGroupIndexCache()
			invalidateSpanIndexCache()
		}
	}
}

