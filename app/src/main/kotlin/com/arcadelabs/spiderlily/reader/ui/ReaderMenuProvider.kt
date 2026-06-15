package com.arcadelabs.spiderlily.reader.ui

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import com.arcadelabs.spiderlily.R

class ReaderMenuProvider(
	private val viewModel: ReaderViewModel,
) : MenuProvider {

	override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
		menuInflater.inflate(R.menu.opt_reader, menu)
	}

	override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
		return when (menuItem.itemId) {
			R.id.action_info -> {
				// TODO
				true
			}

			else -> false
		}
	}
}
