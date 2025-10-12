package com.amary.poke.mobile.presentation.splash

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.amary.poke.mobile.presentation.home.HomeRoute
import com.amary.poke.mobile.presentation.login.LoginRoute
import com.amary.poke.mobile.presentation.register.RegisterScreen
import com.amary.poke.mobile.presentation.register.RegisterViewModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
@SerialName("splash")
data object SplashRoute

fun NavGraphBuilder.splashScreen(navController: NavHostController) {
    composable<SplashRoute> {
        val viewModel: SplashViewModel = koinViewModel()

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is SplashEvent.Loading,
                    is SplashEvent.LoadingComplete -> Unit
                    is SplashEvent.Success -> {
                        navController.navigate(HomeRoute)
                    }
                    is SplashEvent.Error -> {
                        navController.navigate(LoginRoute)
                    }
                }
            }
        }

        SplashScreen()
    }
}