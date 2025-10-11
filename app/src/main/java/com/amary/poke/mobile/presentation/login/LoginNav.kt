package com.amary.poke.mobile.presentation.login

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
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
                //TODO: navigate to home
            },
            onNavigateToRegister = {
                navController.navigate(RegisterRoute)
            }
        )
    }
}