package com.alpharays.medico.v2.navigation

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.alpharays.alaskagemsdk.core.register
import com.alpharays.medico.medico_utils.MedicoConstants
import com.alpharays.medico.profile.v2.di.DependencyProviderProfile
import com.alpharays.medico.v2.di.DependencyProvider

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val context = LocalContext.current

    var startDestination = DependencyProvider.authFeature().authRoute
    if (isAuthorized(context)) startDestination =
        DependencyProvider.homeFeature().homeRoute

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        register(
            DependencyProvider.homeFeature(),
            navController = navController,
            modifier = modifier
        )

        register(
            DependencyProviderProfile.profileFeature(),
            navController,
            modifier
        )

        register(
            DependencyProvider.communityFeature(),
            navController,
            modifier
        )

        register(
            DependencyProvider.authFeature(),
            navController,
            modifier
        )

    }
}

@Composable
fun isAuthorized(context: Context): Boolean {
    val authTokenSharedPref = context.getSharedPreferences(
        MedicoConstants.AUTH_TOKEN_SHARED_PREF,
        ComponentActivity.MODE_PRIVATE
    )
    val token = authTokenSharedPref.getString(MedicoConstants.AUTH_TOKEN_KEY, null)
    return !token.isNullOrEmpty()
}
