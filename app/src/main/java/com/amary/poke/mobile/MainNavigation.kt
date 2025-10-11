package com.amary.poke.mobile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.amary.poke.mobile.presentation.login.LoginRoute
import com.amary.poke.mobile.presentation.login.loginScreen
import com.amary.poke.mobile.presentation.register.registerScreen

@Composable
fun MainNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LoginRoute,
        modifier = modifier
    ) {
        loginScreen(navController)
        registerScreen(navController)
        //TODO: Add navigation here
    }
}