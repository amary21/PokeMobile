package com.amary.poke.mobile.route

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.amary.poke.mobile.presentation.list.ListRoute
import com.amary.poke.mobile.presentation.list.listScreen
import com.amary.poke.mobile.presentation.profile.profileScreen

@Composable
fun HomeNavigation(
    parentNavController: NavHostController,
    tabNavController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = tabNavController,
        startDestination = ListRoute,
        modifier = modifier
    ) {
        listScreen(parentNavController)
        profileScreen(parentNavController)
    }
}