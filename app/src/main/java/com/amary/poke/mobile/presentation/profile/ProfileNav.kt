package com.amary.poke.mobile.presentation.profile

import androidx.compose.material3.Text
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.amary.poke.mobile.presentation.login.LoginRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
@SerialName("profile")
data object ProfileRoute

fun NavGraphBuilder.profileScreen(navController: NavHostController) {
    composable<ProfileRoute> {
        val viewModel: ProfileViewModel = koinViewModel()
        val state = viewModel.state.collectAsStateWithLifecycle()

        ProfileScreen(
            state = state.value,
            events = viewModel.events,
            onGetProfile = viewModel::getProfile,
            onTapLogout = viewModel::logout,
            onLogout = {
                navController.popBackStack()
                navController.navigate(LoginRoute)
            }
        )
    }
}