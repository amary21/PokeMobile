package com.amary.poke.mobile.presentation.profile

import androidx.compose.material3.Text
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("profile")
data object ProfileRoute

fun NavGraphBuilder.profileScreen(navController: NavHostController) {
    composable<ProfileRoute> {
        Text("Profile Screen")
    }
}