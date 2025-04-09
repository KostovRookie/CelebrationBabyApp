package com.stanga.nanit.ui.surface

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.stanga.nanit.navigation.AppNavGraph
import com.stanga.nanit.ui.details.BirthdayViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScaffoldMainScreen() {
    val navController = rememberNavController()
    val viewModel: BirthdayViewModel = koinViewModel()

    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        AppNavGraph(
            navController = navController,
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}