package com.arcadelabs.spiderlily.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcadelabs.spiderlily.core.designsystem.MangaPosterCard
import com.arcadelabs.spiderlily.core.designsystem.SpiderLilyBottomBar
import com.arcadelabs.spiderlily.core.designsystem.SpiderLilyFilterRow
import com.arcadelabs.spiderlily.core.designsystem.SpiderLilySearchBar
import com.arcadelabs.spiderlily.features.home.domain.model.HomeManga
import com.arcadelabs.spiderlily.features.home.domain.model.HomeSection
import com.arcadelabs.spiderlily.ui.theme.SurfaceHighest
import com.arcadelabs.spiderlily.ui.theme.VelvetBlack
import com.arcadelabs.spiderlily.ui.theme.WarmIvory

@Composable
fun HomeRoute(
    selectedNavIndex: Int,
    onNavItemSelected: (Int) -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    HomeScreen(
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onFilterSelected = viewModel::onFilterSelected,
        onNavItemSelected = onNavItemSelected,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    selectedNavIndex: Int,
    onFilterSelected: (String) -> Unit,
    onNavItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = VelvetBlack,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            SpiderLilyBottomBar(
                selectedIndex = selectedNavIndex,
                onItemSelected = onNavItemSelected,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {},
                containerColor = SurfaceHighest,
                contentColor = WarmIvory,
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                    )
                },
                text = {
                    Text(
                        text = "Continue",
                        fontWeight = FontWeight.Bold,
                    )
                },
            )
        },
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 105.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(VelvetBlack)
                .padding(innerPadding),
            contentPadding = PaddingValues(top = 24.dp, bottom = 116.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SpiderLilySearchBar(
                    query = uiState.searchQuery,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                SpiderLilyFilterRow(
                    filters = uiState.filters,
                    selectedFilter = uiState.selectedFilter,
                    onFilterClick = onFilterSelected,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            uiState.sections.forEach { section ->
                item(key = section.title, span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = section.title,
                        color = WarmIvory,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                items(section.manga, key = { "${section.title}_${it.id}" }) { manga ->
                    MangaPosterCard(
                        title = manga.title,
                        source = manga.source,
                        subtitle = manga.chapterLabel,
                        progressPercent = manga.progressPercent,
                        accent = manga.accentColor,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
