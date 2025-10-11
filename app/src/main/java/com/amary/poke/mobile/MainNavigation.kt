package com.amary.poke.mobile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.amary.poke.mobile.presentation.login.LoginRoute
import com.amary.poke.mobile.presentation.login.loginScreen

@Composable
fun MainNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LoginRoute,
        modifier = modifier
    ) {
        loginScreen(navController)

        //TODO: Add navigation here
    }
}