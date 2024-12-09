package com.alpharays.medico.presentation.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpharays.alaskagemsdk.network.ResponseResult
import com.alpharays.medico.domain.model.homescreen.currappointment.Appointment
import com.alpharays.medico.domain.model.homescreen.currappointment.AppointmentData
import com.alpharays.medico.domain.model.homescreen.currappointment.AppointmentDetailsResponse
import com.alpharays.medico.domain.usecase.HomeScreenUseCase
import com.alpharays.medico.medico_utils.MedicoConstants.UNEXPECTED_ERROR
import com.alpharays.medico.medico_utils.MedicoUtils
import com.alpharays.medico.medico_utils.connectivity.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeScreenUseCase: HomeScreenUseCase
) : ViewModel() {
    private val _remoteHomeAppointmentListState = MutableStateFlow(HomeAppointmentsState())
    val remoteHomeAppointmentListStateFlow: StateFlow<HomeAppointmentsState> = _remoteHomeAppointmentListState.asStateFlow()

    private val _addNewAppointmentState = MutableStateFlow(AddNewAppointmentsState())
    val addNewAppointmentStateFlow: StateFlow<AddNewAppointmentsState> = _addNewAppointmentState.asStateFlow()

    private val _cachedAppointmentListState = MutableStateFlow(HomeAppointmentsState())
    val cachedAppointmentListStateFlow: StateFlow<HomeAppointmentsState> = _cachedAppointmentListState.asStateFlow()

    private val _networkStatus = MutableStateFlow(ConnectivityObserver.Status.Lost)
    private val networkStatus: StateFlow<ConnectivityObserver.Status> = _networkStatus

    private val _appointmentListCached = MutableStateFlow(false)
    private var remoteCallCount = 0

    private var token: String? = null

    val combinedAppointmentListState = _cachedAppointmentListState.combine(_remoteHomeAppointmentListState) { cachedData, remoteData ->
//        println("cached Data : A1 : $cachedData")
//        println("remote Data : A2 : $remoteData")

        remoteData.data?.data?.let { aptListData ->
            aptListData[0].appointmentList?.let {
                cachedData.copy(
                    isLoading = remoteData.isLoading,
                    data = AppointmentDetailsResponse(
                        success = remoteData.data?.success,
                        error = remoteData.data?.error,
                        errorList = remoteData.data?.errorList,
                        data = aptListData
                    ),
                    error = remoteData.error
                )
            } ?: cachedData
        } ?: cachedData
    }
    .onEach { _cachedAppointmentListState.emit(it) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000), _cachedAppointmentListState.value)

    init {
        token = MedicoUtils.getAuthToken()
    }

    fun updateNetworkStatus(status: ConnectivityObserver.Status) {
        _networkStatus.value = status
        if(status == ConnectivityObserver.Status.Available && remoteCallCount == 0){
            /**
             * runs only once - when a screen is visible for the first time
             * and other times when the network status is changed,
             * retry remote functions are executed
             */
            remoteCallCount++
            getRemoteAppointmentList()
        }
    }

    fun retryGettingRemoteAppointments() = getRemoteAppointmentList()

    private fun getRemoteAppointmentList() {
        token?.let {
            homeScreenUseCase(it).onEach { result ->
                when (result) {
                    is ResponseResult.Loading -> {
                        val state = HomeAppointmentsState(isLoading = true)
                        _remoteHomeAppointmentListState.value = state
                    }

                    is ResponseResult.Success -> {
                        val state = HomeAppointmentsState(data = result.data)

                        println("remote call count : $remoteCallCount")
                        _remoteHomeAppointmentListState.value = state
                    }

                    is ResponseResult.Error -> {
                        val error = result.message ?: UNEXPECTED_ERROR
                        val state = HomeAppointmentsState(error = error)
                        _remoteHomeAppointmentListState.value = state
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun addNewAppointment(docId: String, newAppointment: Appointment) {
        homeScreenUseCase.addNewAppointment(docId, newAppointment).onEach { result ->
                when (result) {
                    is ResponseResult.Loading -> {
                        _addNewAppointmentState.value = AddNewAppointmentsState(isLoading = true)
                    }

                    is ResponseResult.Success -> {
                        _addNewAppointmentState.value = AddNewAppointmentsState(isSuccess = result.data)
                    }

                    is ResponseResult.Error -> {
                        _addNewAppointmentState.value = AddNewAppointmentsState(error = result.message ?: UNEXPECTED_ERROR)
                    }
                }
            }.launchIn(viewModelScope)
    }


    //  ************   room db - cached data   ************


}


data class HomeAppointmentsState(
    var isLoading: Boolean? = false,
    var data: AppointmentDetailsResponse? = null,
    var error: String? = null
)

data class AddNewAppointmentsState(
    var isLoading: Boolean = false,
    var isSuccess: Any? = null,
    var error: String? = null
)