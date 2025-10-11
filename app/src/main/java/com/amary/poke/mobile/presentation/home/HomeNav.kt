package com.amary.poke.mobile.presentation.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amary.poke.mobile.presentation.list.ListRoute
import com.amary.poke.mobile.presentation.profile.ProfileRoute
import com.amary.poke.mobile.route.HomeNavigation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("home")
data object HomeRoute

fun NavGraphBuilder.homeScreen(navController: NavHostController) {
    composable<HomeRoute> {
        val tabNavController = rememberNavController()
        var selectedTabIndex by remember { mutableIntStateOf(0) }

        val tabTitles = listOf("Home", "Profile")
        val tabRoutes = listOf(ListRoute, ProfileRoute)
        val tabIcons = listOf(Icons.Default.Home, Icons.Default.Person)

        HomeScreen(
            tabTitles = tabTitles,
            tabIcons = tabIcons,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { index ->
                selectedTabIndex = index
                tabNavController.navigate(tabRoutes[index]) {
                    popUpTo(tabNavController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            contentNavigation = { paddingValues ->
                HomeNavigation(
                    parentNavController = navController,
                    tabNavController = tabNavController,
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                )
            }
        )
    }
}