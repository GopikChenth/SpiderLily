package com.arcadelabs.spiderlily.main.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcadelabs.spiderlily.core.designsystem.SpiderLilyBottomBar
import com.arcadelabs.spiderlily.features.explore.ui.ExploreRoute
import com.arcadelabs.spiderlily.features.explore.ui.ExploreSearchResults
import com.arcadelabs.spiderlily.features.explore.ui.ExploreViewModel
import com.arcadelabs.spiderlily.features.feed.ui.FeedRoute
import com.arcadelabs.spiderlily.features.feed.ui.FeedSearchResults
import com.arcadelabs.spiderlily.features.feed.ui.FeedViewModel
import com.arcadelabs.spiderlily.features.home.ui.HomeRoute
import com.arcadelabs.spiderlily.features.home.ui.HomeViewModel
import com.arcadelabs.spiderlily.features.home.ui.SearchResultsContent
import com.arcadelabs.spiderlily.features.library.ui.LibraryRoute
import com.arcadelabs.spiderlily.features.library.ui.LibrarySearchResults
import com.arcadelabs.spiderlily.features.library.ui.LibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpiderLilyApp() {
    var selectedNavIndex by rememberSaveable { mutableIntStateOf(0) }

    // Hoist ViewModels to preserve state during tab transitions
    val homeViewModel: HomeViewModel = hiltViewModel()
    val libraryViewModel: LibraryViewModel = viewModel()
    val exploreViewModel: ExploreViewModel = viewModel()
    val feedViewModel: FeedViewModel = viewModel()

    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val libraryUiState by libraryViewModel.uiState.collectAsStateWithLifecycle()
    val exploreUiState by exploreViewModel.uiState.collectAsStateWithLifecycle()
    val feedUiState by feedViewModel.uiState.collectAsStateWithLifecycle()

    var searchExpandedLibrary by rememberSaveable { mutableStateOf(false) }
    var searchExpandedExplore by rememberSaveable { mutableStateOf(false) }
    var searchExpandedFeed by rememberSaveable { mutableStateOf(false) }

    val isSearchExpanded = when (selectedNavIndex) {
        0 -> homeUiState.isSearchExpanded
        1 -> searchExpandedLibrary
        2 -> searchExpandedExplore
        3 -> searchExpandedFeed
        else -> false
    }

    val activeQuery = when (selectedNavIndex) {
        0 -> homeUiState.searchQuery
        1 -> libraryUiState.searchQuery
        2 -> exploreUiState.searchQuery
        3 -> feedUiState.searchQuery
        else -> ""
    }

    val activePlaceholder = when (selectedNavIndex) {
        0 -> "Search manga"
        1 -> "Search library"
        2 -> "Search sources"
        3 -> "Search updates"
        else -> "Search"
    }

    val setSearchExpanded: (Boolean) -> Unit = { expanded ->
        when (selectedNavIndex) {
            0 -> homeViewModel.onSearchExpandedChange(expanded)
            1 -> {
                searchExpandedLibrary = expanded
                if (!expanded) libraryViewModel.onSearchQueryChanged("")
            }
            2 -> {
                searchExpandedExplore = expanded
                if (!expanded) exploreViewModel.onSearchQueryChanged("")
            }
            3 -> {
                searchExpandedFeed = expanded
                if (!expanded) feedViewModel.onSearchQueryChanged("")
            }
        }
    }

    val onQueryChange: (String) -> Unit = { query ->
        when (selectedNavIndex) {
            0 -> homeViewModel.onSearchQueryChange(query)
            1 -> libraryViewModel.onSearchQueryChanged(query)
            2 -> exploreViewModel.onSearchQueryChanged(query)
            3 -> feedViewModel.onSearchQueryChanged(query)
        }
    }

    if (isSearchExpanded) {
        BackHandler {
            setSearchExpanded(false)
        }
    }

    val density = LocalDensity.current
    val bottomBarHeight = 80.dp
    val bottomBarHeightPx = with(density) { bottomBarHeight.toPx() }

    // Sliding animation matching Futon's duration and easing
    val translationY by animateFloatAsState(
        targetValue = if (isSearchExpanded) bottomBarHeightPx else 0f,
        animationSpec = tween(
            durationMillis = if (isSearchExpanded) 175 else 225,
            easing = if (isSearchExpanded) FastOutLinearInEasing else LinearOutSlowInEasing
        ),
        label = "BottomBarTranslation"
    )

    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    val yOffset by animateDpAsState(
        targetValue = if (isSearchExpanded) 0.dp else statusBarHeight + 8.dp,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "SearchBarYOffset"
    )

    val xPadding by animateDpAsState(
        targetValue = if (isSearchExpanded) 0.dp else 16.dp,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "SearchBarXPadding"
    )

    val topInputFieldPadding by animateDpAsState(
        targetValue = if (isSearchExpanded) statusBarHeight else 0.dp,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "SearchBarInputFieldTopPadding"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            SpiderLilyBottomBar(
                selectedIndex = selectedNavIndex,
                onItemSelected = { selectedNavIndex = it },
                modifier = Modifier
                    .graphicsLayer {
                        this.translationY = translationY
                    }
            )
        },
        floatingActionButton = {
            if (selectedNavIndex == 0 && !isSearchExpanded) {
                ExtendedFloatingActionButton(
                    onClick = {},
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
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
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = selectedNavIndex,
                transitionSpec = {
                    // MaterialFadeThrough transition simulation
                    fadeIn(animationSpec = tween(durationMillis = 220, delayMillis = 90)) +
                            scaleIn(initialScale = 0.92f, animationSpec = tween(durationMillis = 220, delayMillis = 90)) togetherWith
                            fadeOut(animationSpec = tween(durationMillis = 90))
                },
                label = "ScreenTransition"
            ) { targetIndex ->
                when (targetIndex) {
                    0 -> HomeRoute(
                        selectedNavIndex = targetIndex,
                        onNavItemSelected = { selectedNavIndex = it },
                        contentPadding = innerPadding,
                        viewModel = homeViewModel
                    )

                    1 -> LibraryRoute(
                        selectedNavIndex = targetIndex,
                        onNavItemSelected = { selectedNavIndex = it },
                        contentPadding = innerPadding,
                        viewModel = libraryViewModel
                    )

                    2 -> ExploreRoute(
                        selectedNavIndex = targetIndex,
                        onNavItemSelected = { selectedNavIndex = it },
                        contentPadding = innerPadding,
                        viewModel = exploreViewModel
                    )

                    3 -> FeedRoute(
                        selectedNavIndex = targetIndex,
                        onNavItemSelected = { selectedNavIndex = it },
                        contentPadding = innerPadding,
                        viewModel = feedViewModel
                    )
                }
            }

            // Top-level single SearchBar — stays static during page transitions
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = activeQuery,
                        onQueryChange = onQueryChange,
                        onSearch = { /* no-op, live search */ },
                        expanded = isSearchExpanded,
                        onExpandedChange = setSearchExpanded,
                        placeholder = {
                            Text(
                                text = activePlaceholder,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            )
                        },
                        leadingIcon = {
                            if (isSearchExpanded) {
                                IconButton(onClick = { setSearchExpanded(false) }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        },
                        trailingIcon = {
                            if (isSearchExpanded) {
                                val isSearching = selectedNavIndex == 0 && homeUiState.isSearching
                                if (isSearching) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        strokeWidth = 2.dp,
                                    )
                                } else if (activeQuery.isNotEmpty()) {
                                    IconButton(onClick = { onQueryChange("") }) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Clear",
                                            tint = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                }
                            } else {
                                if (selectedNavIndex == 0) {
                                    Row {
                                        IconButton(onClick = { }) {
                                            Icon(
                                                imageVector = Icons.Filled.Download,
                                                contentDescription = "Downloads",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                        IconButton(onClick = { }) {
                                            Icon(
                                                imageVector = Icons.Filled.MoreVert,
                                                contentDescription = "More",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.padding(top = topInputFieldPadding)
                    )
                },
                expanded = isSearchExpanded,
                onExpandedChange = setSearchExpanded,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .offset { IntOffset(0, yOffset.roundToPx()) }
                    .padding(
                        start = xPadding,
                        top = 0.dp,
                        end = xPadding,
                        bottom = if (isSearchExpanded) 0.dp else 8.dp
                    ),
                tonalElevation = 0.dp,
                windowInsets = WindowInsets(0.dp),
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    dividerColor = Color.Transparent,
                ),
            ) {
                // Dynamically render the search results according to the active tab
                when (selectedNavIndex) {
                    0 -> SearchResultsContent(uiState = homeUiState)
                    1 -> LibrarySearchResults(uiState = libraryUiState)
                    2 -> ExploreSearchResults(uiState = exploreUiState, onTogglePinSource = exploreViewModel::togglePinSource)
                    3 -> FeedSearchResults(uiState = feedUiState, onItemClick = feedViewModel::markAsRead)
                }
            }
        }
    }
}

@Composable
private fun PlaceholderRoute(
    title: String,
    selectedNavIndex: Int,
    onNavItemSelected: (Int) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            SpiderLilyBottomBar(
                selectedIndex = selectedNavIndex,
                onItemSelected = onNavItemSelected,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Coming next",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
