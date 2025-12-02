package com.icure.cardinal.compose.multiplatform.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WelcomeState(
    val userName: String = "",
    val availableActions: List<String> = listOf(
        "Action 1",
        "Action 2",
        "Action 3",
        "Action 4"
    )
)

sealed interface WelcomeIntent {
    data class ActionClicked(val action: String) : WelcomeIntent
    data class SetUserName(val userName: String) : WelcomeIntent
    data object Logout : WelcomeIntent
}

class WelcomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(WelcomeState())
    val state: StateFlow<WelcomeState> = _state.asStateFlow()

    fun processIntent(intent: WelcomeIntent) {
        viewModelScope.launch {
            when (intent) {
                is WelcomeIntent.ActionClicked -> handleActionClicked(intent.action)
                is WelcomeIntent.SetUserName -> handleSetUserName(intent.userName)
                is WelcomeIntent.Logout -> handleLogout()
            }
        }
    }

    private fun handleActionClicked(action: String) {
        // TODO: Implement action handling based on the action string
        when (action) {
            "Action 1" -> {
                // Handle action 1
            }
            "Action 2" -> {
                // Handle action 2
            }
            "Action 3" -> {
                // Handle action 3
            }
            "Action 4" -> {
                // Handle action 4
            }
        }
    }

    private fun handleSetUserName(userName: String) {
        _state.update { it.copy(userName = userName) }
    }

    private fun handleLogout() {
        // TODO: Implement logout logic (clear user session, navigate to login, etc.)
    }
}

