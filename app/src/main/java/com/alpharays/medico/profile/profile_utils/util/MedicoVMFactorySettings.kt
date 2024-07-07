package com.alpharays.medico.profile.profile_utils.util

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alpharays.medico.profile.domain.usecase.ProfileScreenUseCase
import com.alpharays.medico.profile.presentation.profile_screen.ProfileViewModel
import kotlin.system.exitProcess

@Composable
inline fun <reified T : ViewModel> getMedicoViewModel(useCase: Any): T {
    return viewModel(
        factory = MedicoViewModelFactory(useCase)
    )
}



@Suppress("UNCHECKED_CAST")
class MedicoViewModelFactory<T>(private val useCase: T) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(useCase as ProfileScreenUseCase) as T
            }

            else -> {
                Log.e("SOME_VIEW_MODEL", "NOT DEFINED")
                println("ViewModelFactory :: view model not defined :: HINT -> may be you are trying to get a viewmodel which is in another package/module ? :)")
                exitProcess(1)
            }
        }
    }
}
