package com.amary.poke.mobile.presentation.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.amary.poke.mobile.domain.model.UserModel
import com.amary.poke.mobile.presentation.component.ProgressDialog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileState,
    events: Flow<ProfileEvent>,
    onGetProfile: () -> Unit,
    onTapLogout: () -> Unit,
    onLogout: () -> Unit = {},
) {
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        onGetProfile()
    }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is ProfileEvent.Loading -> {
                    isLoading = true
                }
                is ProfileEvent.LoadingComplete -> {
                    isLoading = false
                }
                is ProfileEvent.Success -> {
                    Toast.makeText(
                        context,
                        "Logout successful!",
                        Toast.LENGTH_SHORT
                    ).show()
                    onLogout()
                }
                is ProfileEvent.Error -> {
                    Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is ProfileState.Loading -> {
                isLoading = true
            }
            is ProfileState.Success -> {
                isLoading = false
                val user = state.user
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "User Information",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Text(
                                text = "Username",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user.userName,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Full Name",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user.fullName,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Email",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user.email,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onTapLogout,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Logout")
                    }
                }
            }
            is ProfileState.Error -> {
                isLoading = false
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .align(Alignment.Center)
                )
            }
            else -> {
                // Initial state, do nothing
            }
        }
    }

    ProgressDialog(
        isShowing = isLoading,
        message = "Loading profile..."
    )
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        state = ProfileState.Success(
            user = UserModel(
                userName = "Amary",
                fullName = "<NAME>",
                email = "<EMAIL>"
            )
        ),
        events = emptyFlow(),
        onGetProfile = {},
        onTapLogout = {}
    )
}