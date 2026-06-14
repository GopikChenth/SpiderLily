package com.arcadelabs.spiderlily.features.favourites.ui

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
import androidx.compose.foundation.lazy.LazyColumn
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
import com.arcadelabs.spiderlily.features.favourites.domain.model.FavouriteManga
import com.arcadelabs.spiderlily.ui.theme.MutedText
import com.arcadelabs.spiderlily.ui.theme.VelvetBlack
import com.arcadelabs.spiderlily.ui.theme.WarmIvory

@Composable
fun FavouritesRoute(
    selectedNavIndex: Int,
    onNavItemSelected: (Int) -> Unit,
    viewModel: FavouritesViewModel = viewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    FavouritesScreen(
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onCategorySelected = viewModel::onCategorySelected,
        onNavItemSelected = onNavItemSelected,
    )
}

@Composable
fun FavouritesScreen(
    uiState: FavouritesUiState,
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(VelvetBlack)
                .padding(innerPadding),
            contentPadding = PaddingValues(top = 24.dp, bottom = 116.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                SpiderLilySearchBar(
                    query = uiState.searchQuery,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                SpiderLilyFilterRow(
                    filters = uiState.categories.map { it.title },
                    selectedFilter = uiState.selectedCategoryTitle,
                    onFilterClick = { title ->
                        uiState.categories
                            .firstOrNull { it.title == title }
                            ?.let { onCategorySelected(it.id) }
                    },
                )
            }
            item {
                FavouritesHeader(
                    count = uiState.favourites.size,
                    categoryTitle = uiState.selectedCategoryTitle,
                )
            }
            if (uiState.favourites.isEmpty()) {
                item {
                    EmptyFavouritesState()
                }
            } else {
                uiState.favourites.chunked(2).forEachIndexed { index, rowItems ->
                    item(key = "favourites-row-$index") {
                        FavouritesCardRow(items = rowItems)
                    }
                }
            }
        }
    }
}

@Composable
private fun FavouritesHeader(
    count: Int,
    categoryTitle: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Favourites",
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
private fun FavouritesCardRow(
    items: List<FavouriteManga>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        items.forEach { manga ->
            MangaPosterCard(
                title = manga.title,
                source = manga.source,
                subtitle = manga.subtitle,
                progressPercent = manga.progressPercent,
                accent = manga.accentColor,
                modifier = Modifier.weight(1f),
            )
        }
        if (items.size == 1) {
            Box(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun EmptyFavouritesState(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 40.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "No favourites here",
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

private val FavouriteManga.subtitle: String
    get() = if (unreadChapters > 0) {
        "$latestChapter - $unreadChapters unread"
    } else {
        latestChapter
    }
