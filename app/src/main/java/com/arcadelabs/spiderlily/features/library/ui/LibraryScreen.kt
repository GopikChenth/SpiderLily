package com.arcadelabs.spiderlily.features.library.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcadelabs.spiderlily.core.designsystem.MangaPosterCard
import com.arcadelabs.spiderlily.core.designsystem.SpiderLilyFilterRow
import com.arcadelabs.spiderlily.features.library.domain.model.LibraryManga

@Composable
fun LibraryRoute(
    selectedNavIndex: Int,
    onNavItemSelected: (Int) -> Unit,
    contentPadding: PaddingValues,
    viewModel: LibraryViewModel = viewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    LibraryScreen(
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onCategorySelected = viewModel::onCategorySelected,
        onNavItemSelected = onNavItemSelected,
        contentPadding = contentPadding,
    )
}

@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    selectedNavIndex: Int,
    onCategorySelected: (String) -> Unit,
    onNavItemSelected: (Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Spacer(modifier = Modifier.height(72.dp))
            SpiderLilyFilterRow(
                filters = uiState.categories.map { it.title },
                selectedFilter = uiState.selectedCategoryTitle,
                onFilterClick = { title ->
                    uiState.categories
                        .firstOrNull { it.title == title }
                        ?.let { onCategorySelected(it.id) }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 105.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 116.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
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
                            subtitle = null,
                            progressPercent = manga.progressPercent,
                            accent = manga.accentColor,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LibrarySearchResults(
    uiState: LibraryUiState,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 105.dp),
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (uiState.libraryManga.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No library items match \"${uiState.searchQuery}\"",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        } else {
            items(uiState.libraryManga, key = { it.id }) { manga ->
                MangaPosterCard(
                    title = manga.title,
                    source = manga.source,
                    subtitle = null,
                    progressPercent = manga.progressPercent,
                    accent = manga.accentColor,
                    modifier = Modifier.fillMaxWidth(),
                )
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
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "$categoryTitle - $count saved",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
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
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Pick another category or add manga from Explore.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
