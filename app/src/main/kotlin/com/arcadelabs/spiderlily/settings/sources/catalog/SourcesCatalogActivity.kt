package com.arcadelabs.spiderlily.settings.sources.catalog

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.model.getTitle
import com.arcadelabs.spiderlily.core.model.titleResId
import com.arcadelabs.spiderlily.core.nav.router
import com.arcadelabs.spiderlily.core.ui.BaseActivity
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.core.ui.util.FadingAppbarMediator
import com.arcadelabs.spiderlily.core.ui.util.ReversibleActionObserver
import com.arcadelabs.spiderlily.core.ui.widgets.ChipsView
import com.arcadelabs.spiderlily.core.ui.widgets.ChipsView.ChipModel
import com.arcadelabs.spiderlily.core.util.LocaleComparator
import com.arcadelabs.spiderlily.core.util.ext.getDisplayName
import com.arcadelabs.spiderlily.core.util.ext.observe
import com.arcadelabs.spiderlily.core.util.ext.observeEvent
import com.arcadelabs.spiderlily.core.util.ext.toLocale
import com.arcadelabs.spiderlily.databinding.ActivitySourcesCatalogBinding
import com.arcadelabs.spiderlily.list.ui.adapter.TypedListSpacingDecoration
import com.arcadelabs.spiderlily.main.ui.owners.AppBarOwner
import com.arcadelabs.spiderlily_parser.model.ContentType

@AndroidEntryPoint
class SourcesCatalogActivity : BaseActivity<ActivitySourcesCatalogBinding>(),
	OnListItemClickListener<SourceCatalogItem.Source>,
	AppBarOwner,
	MenuItem.OnActionExpandListener,
	ChipsView.OnChipClickListener {

	override val appBar: AppBarLayout
		get() = viewBinding.appbar

	private val viewModel by viewModels<SourcesCatalogViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(ActivitySourcesCatalogBinding.inflate(layoutInflater))
		setDisplayHomeAsUp(isEnabled = true, showUpAsClose = false)
		val sourcesAdapter = SourcesCatalogAdapter(this)
		with(viewBinding.recyclerView) {
			setHasFixedSize(true)
			addItemDecoration(TypedListSpacingDecoration(context, false))
			adapter = sourcesAdapter
		}
		viewBinding.chipsFilter.onChipClickListener = this
		FadingAppbarMediator(viewBinding.appbar, viewBinding.toolbar).bind()
		viewModel.content.observe(this, sourcesAdapter)
		viewModel.onActionDone.observeEvent(
			this,
			ReversibleActionObserver(viewBinding.recyclerView),
		)
		combine(viewModel.appliedFilter, viewModel.hasNewSources, viewModel.contentTypes, ::Triple).observe(this) {
			updateFilers(it.first, it.second, it.third)
		}
		addMenuProvider(SourcesCatalogMenuProvider(this, viewModel, this))
	}

	override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
		val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
		viewBinding.recyclerView.updatePadding(
			left = bars.left,
			right = bars.right,
			bottom = bars.bottom,
		)
		viewBinding.appbar.updatePadding(
			left = bars.left,
			right = bars.right,
			top = bars.top,
		)
		return WindowInsetsCompat.Builder(insets)
			.setInsets(WindowInsetsCompat.Type.systemBars(), Insets.NONE)
			.build()
	}

	override fun onChipClick(chip: Chip, data: Any?) {
		when (data) {
			is ContentType -> viewModel.setContentType(data, !chip.isChecked)
			is Boolean -> viewModel.setNewOnly(!chip.isChecked)
			else -> showLocalesMenu(chip)
		}
	}

	override fun onItemClick(item: SourceCatalogItem.Source, view: View) {
		router.openList(item.source, null, null, item.source.getTitle(this))
	}

	override fun onItemLongClick(item: SourceCatalogItem.Source, view: View): Boolean {
		viewModel.addSource(item.source)
		return false
	}

	override fun onMenuItemActionExpand(item: MenuItem): Boolean {
		val sq = (item.actionView as? SearchView)?.query?.trim()?.toString().orEmpty()
		viewModel.performSearch(sq)
		return true
	}

	override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
		viewModel.performSearch(null)
		return true
	}

	private fun updateFilers(
		appliedFilter: SourcesCatalogFilter,
		hasNewSources: Boolean,
		contentTypes: List<ContentType>,
	) {
		val chips = ArrayList<ChipModel>(contentTypes.size + 2)
		chips += ChipModel(
			title = appliedFilter.locale?.toLocale().getDisplayName(this),
			icon = R.drawable.ic_language,
			isDropdown = true,
		)
		if (hasNewSources) {
			chips += ChipModel(
				title = getString(R.string._new),
				icon = R.drawable.ic_updated,
				isChecked = appliedFilter.isNewOnly,
				data = true,
			)
		}
		contentTypes.mapTo(chips) { type ->
			ChipModel(
				title = getString(type.titleResId),
				isChecked = type in appliedFilter.types,
				data = type,
			)
		}
		viewBinding.chipsFilter.setChips(chips)
	}

	private fun showLocalesMenu(anchor: View) {
		val locales = viewModel.locales.mapTo(ArrayList(viewModel.locales.size)) {
			it to it?.toLocale()
		}
		locales.sortWith(compareBy(nullsFirst(LocaleComparator())) { it.second })
		val menu = PopupMenu(this, anchor)
		for ((i, lc) in locales.withIndex()) {
			menu.menu.add(Menu.NONE, Menu.NONE, i, lc.second.getDisplayName(this))
		}
		menu.setOnMenuItemClickListener {
			viewModel.setLocale(locales.getOrNull(it.order)?.first)
			true
		}
		menu.show()
	}
}
