package com.alpharays.medico.v2.presentation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.alpharays.medico.R
import com.alpharays.medico.profile.v2.di.DependencyProviderProfile
import com.alpharays.medico.v2.di.DependencyProvider

enum class BottomTabs(
    @StringRes
    val title: Int,
    @DrawableRes
    val icon: Int,
    val route: String,
    val description: String
) {

    HOME(
        R.string.app_name,
        R.drawable.home_icon,
        DependencyProvider.homeFeature().homeRoute,
        "home hai"
    ),

    PROFILE(
        R.string.app_name,
        R.drawable.user,
        DependencyProviderProfile.profileFeature().profileRoute,
        "profile hai"
    ),

    COMMUNITY(
    R.string.app_name,
    R.drawable.community,
    DependencyProvider.communityFeature().communityRoute,
    "community hai"
    )

}
