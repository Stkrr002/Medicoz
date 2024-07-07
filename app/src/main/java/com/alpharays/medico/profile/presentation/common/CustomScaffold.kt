package com.alpharays.medico.profile.presentation.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun CustomScaffold(
    isBottomBarPresent: Boolean = true,
    isTopBarPresent: Boolean = false,
    navController: NavController,
    topBarContent: @Composable () -> Unit = {},
    content: @Composable (padding: PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (isTopBarPresent) {
                topBarContent()
            }
        },
//        bottomBar = {
//            if (isBottomBarPresent) {
//                CustomBottomNavigationBar(navController)
//            }
//        }
    ) { innerPadding ->
        content(innerPadding)
    }
}