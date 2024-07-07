package com.alpharays.medico.profile.v2.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.alpharays.medico.profile.presentation.navigation.AppScreens
import com.alpharays.medico.profile.presentation.profile_screen.ProfileScreen
import com.alpharays.medico.profile.presentation.profile_screen.UpdateProfileScreen
import com.alpharays.medico.profile.v2.modules.profile.ProfileFeatureApi

private const val baseRoute = "profile"
private const val profileScenario = "$baseRoute/scenario"

class ProfileFeatureImpl() : ProfileFeatureApi {

    override val profileRoute: String = baseRoute
    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
        modifier: Modifier
    ) {
        navGraphBuilder.composable(baseRoute) {
            ProfileScreen(
                navController
            )
        }

        //nested

        navGraphBuilder.navigation(
            route = profileScenario,
            startDestination = AppScreens.UpdateProfileScreen.route
        ) {
            composable(route = AppScreens.UpdateProfileScreen.route) {
                UpdateProfileScreen(navController = navController)
            }
        }
    }

}