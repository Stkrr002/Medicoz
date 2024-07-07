package com.alpharays.medico.medico_utils

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alpharays.medico.domain.usecase.AuthUseCase
import com.alpharays.medico.domain.usecase.HomeScreenUseCase
import com.alpharays.medico.presentation.auth_screen.AuthViewModel
import com.alpharays.medico.presentation.home_screen.HomeViewModel
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
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(useCase as AuthUseCase) as T
            }

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(useCase as HomeScreenUseCase) as T
            }

//            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
//                ProfileViewModel(useCase as ProfileScreenUseCase) as T
//            }

            else -> {
                Log.e("SOME_VIEW_MODEL", "NOT DEFINED")
                println("MedicoViewModelFactory :: view model not defined")
                exitProcess(1)
            }
        }
    }
}

