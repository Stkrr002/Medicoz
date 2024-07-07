package com.alpharays.medico.profile.profile_utils.connectivity

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConnectivityViewModel : ViewModel() {
    private val _connectivityStatus = MutableStateFlow(ConnectivityObserver.Status.Lost)
    val connectivityStatus: StateFlow<ConnectivityObserver.Status> = _connectivityStatus.asStateFlow()

    fun updateConnectivityStatus(status: ConnectivityObserver.Status) {
        _connectivityStatus.value = status
    }
}
