package com.amary.poke.mobile.presentation.list

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
@SerialName("list")
data object ListRoute

fun NavGraphBuilder.listScreen(navController: NavHostController) {
    composable<ListRoute> {
        val viewModel: ListViewModel = koinViewModel()
        val listState = viewModel.listState.collectAsStateWithLifecycle()
        val searchState = viewModel.searchState.collectAsStateWithLifecycle()

        ListScreen(
            listState = listState.value,
            searchState = searchState.value,
            onGetListItem = viewModel::getList,
            onGetSearch = viewModel::getSearch,
            onItemClick = {
                //TODO
            },
            onNavigateToDetail = {
                //TODO
            }
        )
    }
}