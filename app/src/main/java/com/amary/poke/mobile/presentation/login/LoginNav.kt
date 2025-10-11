package com.amary.poke.mobile.presentation.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.amary.poke.mobile.presentation.home.HomeRoute
import com.amary.poke.mobile.presentation.register.RegisterRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
@SerialName("login")
data object LoginRoute

fun NavGraphBuilder.loginScreen(navController: NavHostController) {
    composable<LoginRoute> {
        val viewModel: LoginViewModel = koinViewModel()

        LoginScreen(
            events = viewModel.events,
            onLogin = viewModel::login,
            onLoginSuccess = {
                navController.navigate(HomeRoute)
            },
            onNavigateToRegister = {
                navController.navigate(RegisterRoute)
            }
        )
    }
}