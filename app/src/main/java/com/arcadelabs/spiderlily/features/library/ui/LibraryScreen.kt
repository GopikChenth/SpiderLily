package com.arcadelabs.spiderlily.features.library.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.arcadelabs.spiderlily.features.library.domain.model.LibraryManga
import com.arcadelabs.spiderlily.ui.theme.MutedText
import com.arcadelabs.spiderlily.ui.theme.VelvetBlack
import com.arcadelabs.spiderlily.ui.theme.WarmIvory

@Composable
fun LibraryRoute(
    selectedNavIndex: Int,
    onNavItemSelected: (Int) -> Unit,
    viewModel: LibraryViewModel = viewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    LibraryScreen(
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onCategorySelected = viewModel::onCategorySelected,
        onNavItemSelected = onNavItemSelected,
    )
}

@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    selectedNavIndex: Int,
    onCategorySelected: (String) -> Unit,
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
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 105.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(VelvetBlack)
                .padding(innerPadding),
            contentPadding = PaddingValues(top = 24.dp, bottom = 116.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SpiderLilySearchBar(
                    query = uiState.searchQuery,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                SpiderLilyFilterRow(
                    filters = uiState.categories.map { it.title },
                    selectedFilter = uiState.selectedCategoryTitle,
                    onFilterClick = { title ->
                        uiState.categories
                            .firstOrNull { it.title == title }
                            ?.let { onCategorySelected(it.id) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = (-16).dp),
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                LibraryHeader(
                    count = uiState.libraryManga.size,
                    categoryTitle = uiState.selectedCategoryTitle,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (uiState.libraryManga.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyLibraryState()
                }
            } else {
                items(uiState.libraryManga, key = { it.id }) { manga ->
                    MangaPosterCard(
                        title = manga.title,
                        source = manga.source,
                        subtitle = manga.subtitle,
                        progressPercent = manga.progressPercent,
                        accent = manga.accentColor,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryHeader(
    count: Int,
    categoryTitle: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Library",
            color = WarmIvory,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$categoryTitle - $count saved",
            color = MutedText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun EmptyLibraryState(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "No items here",
            color = WarmIvory,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Pick another category or add manga from Explore.",
            color = MutedText,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private val LibraryManga.subtitle: String
    get() = if (unreadChapters > 0) {
        "$latestChapter - $unreadChapters unread"
    } else {
        latestChapter
    }
