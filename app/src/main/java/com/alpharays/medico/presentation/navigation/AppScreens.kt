package com.alpharays.medico.presentation.navigation

import com.alpharays.medico.medico_utils.MedicoConstants.APPOINTMENT_COMPLETE_SCREEN_ROUTE
import com.alpharays.medico.medico_utils.MedicoConstants.APPOINTMENT_START_SCREEN_ROUTE
import com.alpharays.medico.medico_utils.MedicoConstants.AUTH_SCREEN_ROUTE
import com.alpharays.medico.medico_utils.MedicoConstants.HOME_SCREEN_ROUTE
import com.alpharays.medico.medico_utils.MedicoConstants.PATIENT_SCREEN_ROUTE
import com.alpharays.medico.medico_utils.MedicoConstants.PROFILE_SCREEN_ROUTE
import com.alpharays.medico.medico_utils.MedicoConstants.UPDATE_PROFILE_SCREEN_ROUTE

sealed class AppScreens(val route: String) {
    data object AuthScreen : AppScreens(AUTH_SCREEN_ROUTE)
    data object HomeScreen : AppScreens(HOME_SCREEN_ROUTE)
    data object PatientScreen : AppScreens(PATIENT_SCREEN_ROUTE)
    data object AppointmentStartScreen : AppScreens(APPOINTMENT_START_SCREEN_ROUTE)
    data object AppointmentCompleteScreen : AppScreens(APPOINTMENT_COMPLETE_SCREEN_ROUTE)
//    data object UpdateProfileScreen : AppScreens(UPDATE_PROFILE_SCREEN_ROUTE)
//    data object ProfileScreen : AppScreens(PROFILE_SCREEN_ROUTE)  // TODO: AppScreen's ProfileScreen route has been changed to "profile"
}