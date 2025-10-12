package com.amary.poke.mobile.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.amary.poke.mobile.R

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
        ,
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.padding(horizontal = 24.dp),
            painter = painterResource(R.drawable.ic_pokemon),
            contentDescription = "Pokemon"
        )
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}