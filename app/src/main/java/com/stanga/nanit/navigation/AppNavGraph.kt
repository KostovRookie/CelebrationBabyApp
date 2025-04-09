package com.stanga.nanit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.stanga.nanit.ui.birthday.NanitCelebrityScreen
import com.stanga.nanit.ui.details.AddBirthdayScreen
import com.stanga.nanit.ui.details.BirthdayDetailsEvent
import com.stanga.nanit.ui.details.BirthdayDetailsState

@Composable
fun AppNavGraph(
    navController: NavHostController,
    state: BirthdayDetailsState,
    onEvent: (BirthdayDetailsEvent) -> Unit
) {
    NavHost(navController = navController, startDestination = "details_screen") {
        composable("details_screen") {
            AddBirthdayScreen(
                state = state,
                onEvent = onEvent,
                navController = navController
            )
        }
        composable("birthday_screen") {
            NanitCelebrityScreen(
                state = state,
                onEvent = onEvent,
                navController = navController
            )
        }
    }
}