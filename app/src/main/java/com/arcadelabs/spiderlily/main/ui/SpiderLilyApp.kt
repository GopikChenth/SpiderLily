package com.arcadelabs.spiderlily.main.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arcadelabs.spiderlily.core.designsystem.SpiderLilyBottomBar
import com.arcadelabs.spiderlily.features.favourites.ui.FavouritesRoute
import com.arcadelabs.spiderlily.features.home.ui.HomeRoute
import com.arcadelabs.spiderlily.ui.theme.MutedText
import com.arcadelabs.spiderlily.ui.theme.VelvetBlack
import com.arcadelabs.spiderlily.ui.theme.WarmIvory

@Composable
fun SpiderLilyApp() {
    var selectedNavIndex by rememberSaveable { mutableIntStateOf(0) }

    when (selectedNavIndex) {
        0 -> HomeRoute(
            selectedNavIndex = selectedNavIndex,
            onNavItemSelected = { selectedNavIndex = it },
        )

        1 -> FavouritesRoute(
            selectedNavIndex = selectedNavIndex,
            onNavItemSelected = { selectedNavIndex = it },
        )

        2 -> PlaceholderRoute(
            title = "Explore",
            selectedNavIndex = selectedNavIndex,
            onNavItemSelected = { selectedNavIndex = it },
        )

        3 -> PlaceholderRoute(
            title = "Feed",
            selectedNavIndex = selectedNavIndex,
            onNavItemSelected = { selectedNavIndex = it },
        )
    }
}

@Composable
private fun PlaceholderRoute(
    title: String,
    selectedNavIndex: Int,
    onNavItemSelected: (Int) -> Unit,
) {
    Scaffold(
        containerColor = VelvetBlack,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            SpiderLilyBottomBar(
                selectedIndex = selectedNavIndex,
                onItemSelected = onNavItemSelected,
            )
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(VelvetBlack),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = title,
                    color = WarmIvory,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Coming next",
                    color = MutedText,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
