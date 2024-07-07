package com.alpharays.medico.profile.presentation.navigation

import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.APPOINTMENT_COMPLETE_SCREEN_ROUTE
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.APPOINTMENT_START_SCREEN_ROUTE
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.AUTH_SCREEN_ROUTE
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.HOME_SCREEN_ROUTE
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.PATIENT_SCREEN_ROUTE
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.PROFILE_SCREEN_ROUTE
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.UPDATE_PROFILE_SCREEN_ROUTE

sealed class AppScreens(val route: String) {
    data object AuthScreen : AppScreens(AUTH_SCREEN_ROUTE)
    data object HomeScreen : AppScreens(HOME_SCREEN_ROUTE)
    data object PatientScreen : AppScreens(PATIENT_SCREEN_ROUTE)
    data object AppointmentStartScreen : AppScreens(APPOINTMENT_START_SCREEN_ROUTE)
    data object AppointmentCompleteScreen : AppScreens(APPOINTMENT_COMPLETE_SCREEN_ROUTE)
    data object ProfileScreen : AppScreens(PROFILE_SCREEN_ROUTE)
    data object UpdateProfileScreen : AppScreens(UPDATE_PROFILE_SCREEN_ROUTE)
}