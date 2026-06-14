package com.arcadelabs.spiderlily.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arcadelabs.spiderlily.ui.theme.MutedText
import com.arcadelabs.spiderlily.ui.theme.Obsidian
import com.arcadelabs.spiderlily.ui.theme.SurfaceRaised
import com.arcadelabs.spiderlily.ui.theme.WarmClay
import com.arcadelabs.spiderlily.ui.theme.WarmIvory

data class SpiderLilyNavItem(
    val label: String,
    val icon: ImageVector,
)

val SpiderLilyHomeNavItems = listOf(
    SpiderLilyNavItem("Home", Icons.Filled.History),
    SpiderLilyNavItem("Library", Icons.Filled.FavoriteBorder),
    SpiderLilyNavItem("Explore", Icons.Filled.Explore),
    SpiderLilyNavItem("Feed", Icons.Filled.RssFeed),
)

@Composable
fun SpiderLilySearchBar(
    query: String,
    modifier: Modifier = Modifier,
    placeholder: String = "Search manga",
    onSearchClick: () -> Unit = {},
    onDownloadsClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF171717),
        shape = RoundedCornerShape(34.dp),
        onClick = onSearchClick,
    ) {
        Row(
            modifier = Modifier
                .height(68.dp)
                .padding(start = 20.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = WarmIvory,
                modifier = Modifier.size(30.dp),
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = query.ifBlank { placeholder },
                color = if (query.isBlank()) WarmIvory else WarmClay,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onDownloadsClick) {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = "Downloads",
                    tint = WarmIvory,
                )
            }
            IconButton(onClick = onMoreClick) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More",
                    tint = WarmIvory,
                )
            }
        }
    }
}

@Composable
fun SpiderLilyFilterRow(
    filters: List<String>,
    selectedFilter: String?,
    onFilterClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(filters) { filter ->
            val selected = filter == selectedFilter
            Surface(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = if (selected) WarmIvory.copy(alpha = 0.72f) else MutedText,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .clickable { onFilterClick(filter) },
                color = Color.Transparent,
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = filter,
                    color = if (selected) WarmIvory else WarmClay,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }
        }
    }
}

@Composable
fun MangaPosterCard(
    title: String,
    source: String,
    progressPercent: Int,
    accent: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: () -> Unit = {},
) {
    Column(modifier = modifier) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            color = SurfaceRaised,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(13f / 18f),
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(accent.copy(alpha = 0.95f), SurfaceRaised, Obsidian),
                        ),
                    )
                    .padding(10.dp),
            ) {
                Text(
                    text = source.take(2).uppercase(),
                    color = WarmIvory,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.52f))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.72f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$progressPercent%",
                        color = WarmIvory,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            color = WarmIvory,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = WarmClay,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun SpiderLilyBottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    items: List<SpiderLilyNavItem> = SpiderLilyHomeNavItems,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color(0xFF151515),
        tonalElevation = 0.dp,
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedIndex,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = WarmIvory,
                    selectedTextColor = WarmIvory,
                    indicatorColor = Color(0xFF4D4D4D),
                    unselectedIconColor = WarmClay,
                    unselectedTextColor = WarmClay,
                ),
            )
        }
    }
}
