package com.amary.poke.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.amary.poke.mobile.presentation.theme.PokeMobileTheme
import com.amary.poke.mobile.route.MainNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeMobileTheme {
                MainNavigation(modifier = Modifier.fillMaxSize())
            }
        }
    }
}