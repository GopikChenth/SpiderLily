package com.arcadelabs.spiderlily.features.explore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcadelabs.spiderlily.features.explore.domain.model.ExploreMangaSource

@Composable
fun ExploreRoute(
    selectedNavIndex: Int,
    onNavItemSelected: (Int) -> Unit,
    contentPadding: PaddingValues,
    viewModel: ExploreViewModel = viewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    ExploreScreen(
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onTogglePinSource = viewModel::togglePinSource,
        onNavItemSelected = onNavItemSelected,
        contentPadding = contentPadding,
    )
}

@Composable
fun ExploreScreen(
    uiState: ExploreUiState,
    selectedNavIndex: Int,
    onTogglePinSource: (String) -> Unit,
    onNavItemSelected: (Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(contentPadding),
            contentPadding = PaddingValues(top = 72.dp, bottom = 116.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // 2. Quick Action Buttons
            item(span = { GridItemSpan(maxLineSpan) }) {
                ExploreQuickButtons(
                    onLocalClick = {},
                    onBookmarksClick = {},
                    onRandomClick = {},
                    onDownloadsClick = {},
                )
            }

            // 3. Suggestions Section
            if (uiState.suggestions.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Suggestions",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                            )
                            Text(
                                text = "More",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.clickable { },
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 0.dp),
                        ) {
                            items(uiState.suggestions, key = { it.id }) { suggestion ->
                                SuggestionMangaCard(
                                    title = suggestion.title,
                                    source = suggestion.source,
                                    tags = suggestion.tags,
                                    accent = suggestion.accentColor,
                                )
                            }
                        }
                    }
                }
            }

            // 4. Pinned Sources
            val pinned = uiState.sources.filter { it.isPinned }
            if (pinned.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Pinned",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                items(pinned, key = { "pinned_${it.id}" }, span = { GridItemSpan(maxLineSpan) }) { source ->
                    ExploreSourceItem(
                        source = source,
                        onTogglePin = { onTogglePinSource(source.id) },
                    )
                }
            }

            // 5. All Sources
            val unpinned = uiState.sources.filter { !it.isPinned }
            if (unpinned.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Sources",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                items(unpinned, key = { "source_${it.id}" }, span = { GridItemSpan(maxLineSpan) }) { source ->
                    ExploreSourceItem(
                        source = source,
                        onTogglePin = { onTogglePinSource(source.id) },
                    )
                }
            }
        }
    }
}

@Composable
fun ExploreSearchResults(
    uiState: ExploreUiState,
    onTogglePinSource: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (uiState.sources.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No sources match \"${uiState.searchQuery}\"",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        } else {
            items(uiState.sources, key = { it.id }) { source ->
                ExploreSourceItem(
                    source = source,
                    onTogglePin = { onTogglePinSource(source.id) },
                )
            }
        }
    }
}

@Composable
fun ExploreQuickButtons(
    onLocalClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    onRandomClick: () -> Unit,
    onDownloadsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            ExploreButton(
                text = "Local Storage",
                icon = Icons.Filled.Folder,
                onClick = onLocalClick,
                modifier = Modifier.weight(1f),
            )
            ExploreButton(
                text = "Bookmarks",
                icon = Icons.Filled.BookmarkBorder,
                onClick = onBookmarksClick,
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            ExploreButton(
                text = "Random",
                icon = Icons.Filled.Casino,
                onClick = onRandomClick,
                modifier = Modifier.weight(1f),
            )
            ExploreButton(
                text = "Downloads",
                icon = Icons.Filled.Download,
                onClick = onDownloadsClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun ExploreButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun SuggestionMangaCard(
    title: String,
    source: String,
    tags: List<String>,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val cardBackground = MaterialTheme.colorScheme.surfaceContainerLow
    val cardSurface = MaterialTheme.colorScheme.surface
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = cardBackground,
        modifier = modifier
            .width(280.dp)
            .height(110.dp),
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Cover Image Box
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(accent.copy(alpha = 0.95f), cardBackground, cardSurface),
                        ),
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = source.take(2).uppercase(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.52f))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                )
            }
            // Title & Tags info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = tags.joinToString(", "),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun ExploreSourceItem(
    source: ExploreMangaSource,
    onTogglePin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Icon Favicon fallback
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = source.name.take(1).uppercase(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = source.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = source.language.uppercase(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            IconButton(onClick = onTogglePin) {
                Icon(
                    imageVector = if (source.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                    contentDescription = "Pin Source",
                    tint = if (source.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
