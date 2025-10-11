package com.amary.poke.mobile.presentation.login

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
@SerialName("login")
data object LoginRoute

fun NavGraphBuilder.loginScreen(navController: NavHostController) {
    composable<LoginRoute> {
        val viewModel: LoginViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        LoginScreen(
            state = state,
            onLogin = { username, password ->
                //TODO: implement login
            },
            onLoginSuccess = {
                //TODO: navigate to home
            },
            onNavigateToRegister = {
                //TODO: navigate to register
            }
        )
    }
}