package com.amary.poke.mobile.route

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.amary.poke.mobile.presentation.detail.detailScreen
import com.amary.poke.mobile.presentation.home.homeScreen
import com.amary.poke.mobile.presentation.login.loginScreen
import com.amary.poke.mobile.presentation.register.registerScreen
import com.amary.poke.mobile.presentation.splash.SplashRoute
import com.amary.poke.mobile.presentation.splash.splashScreen

@Composable
fun MainNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = modifier
    ) {
        splashScreen(navController)
        loginScreen(navController)
        registerScreen(navController)
        homeScreen(navController)
        detailScreen(navController)
    }
}