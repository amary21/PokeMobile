package com.amary.poke.mobile.presentation.list

import androidx.compose.material3.Text
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("list")
data object ListRoute

fun NavGraphBuilder.listScreen(navController: NavHostController) {
    composable<ListRoute> {
        Text("List Screen")
    }
}