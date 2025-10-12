package com.amary.poke.mobile.presentation.detail

import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
@SerialName("detail")
data class DetailRoute(
    val name: String
)

fun NavGraphBuilder.detailScreen(navController: NavHostController) {
    composable<DetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<DetailRoute>()
        val viewModel: DetailViewModel = koinViewModel()
        val state = viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            viewModel.getDetail(route.name)
        }

        DetailScreen(
            state = state.value,
            onNavigateBack = navController::popBackStack
        )
    }
}