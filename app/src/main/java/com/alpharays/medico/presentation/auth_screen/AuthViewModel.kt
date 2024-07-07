package com.alpharays.medico.presentation.auth_screen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpharays.alaskagemsdk.network.ResponseResult
import com.alpharays.medico.domain.model.authscreen.AuthResponse
import com.alpharays.medico.domain.usecase.AuthUseCase
import com.alpharays.medico.medico_utils.MedicoConstants
import com.alpharays.medico.medico_utils.connectivity.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {
    private val _authInfoStateFlow = MutableStateFlow(AuthResponseState())
    val authInfoStateFlow: StateFlow<AuthResponseState> = _authInfoStateFlow

    private val _networkStatus = MutableStateFlow(ConnectivityObserver.Status.Lost)
    val networkStatus: StateFlow<ConnectivityObserver.Status> = _networkStatus

    fun updateNetworkStatus(status: ConnectivityObserver.Status) {
        _networkStatus.value = status
    }

    fun getAuthTokenState(contactNumber: String) {
        if(networkStatus.value == ConnectivityObserver.Status.Available){
            authUseCase(contactNumber).onEach { result ->
                when (result) {
                    is ResponseResult.Loading -> {
                        val state = AuthResponseState(isLoading = true)
                        _authInfoStateFlow.value = state
                    }

                    is ResponseResult.Success -> {
                        val state = AuthResponseState(isSuccess = result.data?.data)
                        _authInfoStateFlow.value = state
                    }

                    is ResponseResult.Error -> {
                        val state = AuthResponseState(isError = result.message ?: MedicoConstants.UNEXPECTED_ERROR)
                        _authInfoStateFlow.value = state
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}


data class AuthResponseState(
    var isLoading: Boolean? = false,
    var isSuccess: AuthResponse? = null,
    var isError: String? = null
)