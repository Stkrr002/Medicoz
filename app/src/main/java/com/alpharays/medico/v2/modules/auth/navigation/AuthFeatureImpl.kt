package com.alpharays.medico.v2.modules.auth.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.alpharays.medico.MedicoApp
import com.alpharays.medico.domain.usecase.AuthUseCase
import com.alpharays.medico.medico_utils.getMedicoViewModel
import com.alpharays.medico.presentation.auth_screen.AuthScreen
import com.alpharays.medico.presentation.auth_screen.AuthViewModel
import com.alpharays.medico.v2.modules.auth.AuthFeatureApi
import javax.inject.Inject

private const val authBaseRoute = "authBaseRoute"

class AuthFeatureImpl @Inject constructor(
    private val authUseCase: AuthUseCase
): AuthFeatureApi {
    override val authRoute = authBaseRoute

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
        modifier: Modifier
    ) {
        navGraphBuilder.composable(route = authBaseRoute) {

            val authViewModel: AuthViewModel = getMedicoViewModel(authUseCase)
            AuthScreen(navController = navController, authViewModel = authViewModel)
        }

    }
}