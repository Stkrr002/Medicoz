package com.alpharays.medico.v2.modules.home.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.alpharays.medico.presentation.appointment_screen.AppointmentCompleteScreen
import com.alpharays.medico.presentation.appointment_screen.AppointmentStartScreen
import com.alpharays.medico.presentation.home_screen.HomeScreen
import com.alpharays.medico.presentation.home_screen.patient.PatientDetailScreen
import com.alpharays.medico.presentation.navigation.AppScreens
import com.alpharays.medico.v2.modules.home.HomeFeatureApi

//todo all screens of this module will be defined here and isolated hoga
private const val baseRoute = "home_screen"
private const val homeRouteScenario = "$baseRoute/scenario"

class HomeFeatureImpl : HomeFeatureApi {

    override val homeRoute = baseRoute

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
        modifier: Modifier
    ) {

        navGraphBuilder.composable(baseRoute) {
            HomeScreen(
                navController,
                modifier
            )
        }

        //nested

        navGraphBuilder.navigation(
            route = homeRouteScenario,
            startDestination = AppScreens.PatientScreen.route
        ) {

            composable(route = AppScreens.PatientScreen.route) {
                PatientDetailScreen(navController = navController)
            }

            composable(route = AppScreens.AppointmentStartScreen.route) {
                AppointmentStartScreen(navController = navController)
            }

            composable(route = AppScreens.AppointmentCompleteScreen.route) {
                AppointmentCompleteScreen(navController = navController)
            }

        }
    }
}