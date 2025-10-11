package com.amary.poke.mobile.presentation.register

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.amary.poke.mobile.presentation.login.LoginRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
@SerialName("register")
data object RegisterRoute

fun NavGraphBuilder.registerScreen(navController: NavHostController) {
    composable<RegisterRoute> {
        val viewModel: RegisterViewModel = koinViewModel()

        RegisterScreen(
            events = viewModel.events,
            onRegister = viewModel::register,
            onRegisterSuccess = {
                navController.popBackStack()
                navController.navigate(LoginRoute)
            },
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}