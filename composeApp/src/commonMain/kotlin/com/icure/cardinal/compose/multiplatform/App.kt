package com.icure.cardinal.compose.multiplatform

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.icure.cardinal.compose.multiplatform.navigation.AuthNavGraph
import com.icure.cardinal.compose.multiplatform.ui.screens.WelcomeScreen
import com.icure.cardinal.compose.multiplatform.ui.viewmodels.AppViewModel
import com.icure.cardinal.compose.multiplatform.ui.viewmodels.AppState
import com.icure.cardinal.compose.multiplatform.ui.viewmodels.WelcomeViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val appViewModel: AppViewModel = viewModel { AppViewModel() }
        val appState by appViewModel.authState.collectAsState()

        when (appState) {
            is AppState.Unauthenticated,
            is AppState.PendingValidation -> {
                AuthNavGraph(appViewModel = appViewModel)
            }
            is AppState.Authenticated -> {
                WelcomeScreen(
                    appViewModel = appViewModel,
                )
            }
        }
    }
}