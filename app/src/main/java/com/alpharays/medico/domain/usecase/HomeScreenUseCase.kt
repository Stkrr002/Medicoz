package com.alpharays.medico.domain.usecase

import com.alpharays.alaskagemsdk.network.ResponseResult
import com.alpharays.medico.domain.model.homescreen.currappointment.Appointment
import com.alpharays.medico.domain.repository.HomeRepository
import com.alpharays.medico.medico_utils.MedicoConstants.SOMETHING_WENT_WRONG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.alpharays.medico.domain.model.homescreen.currappointment.AppointmentDetailsResponse as AptResponse


class HomeScreenUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    operator fun invoke(token: String): Flow<ResponseResult<AptResponse>> = flow {
        emit(ResponseResult.Loading())
        val result: ResponseResult<AptResponse> = try {
            homeRepository.getAppointmentList(token)
        } catch (e: Exception) {
            ResponseResult.Error(SOMETHING_WENT_WRONG)
        }
        emit(result)
    }

    fun addNewAppointment(docId: String, newAppointment: Appointment): Flow<ResponseResult<Any>> =
        flow {
            val response = try {
                homeRepository.addNewAppointment(docId, newAppointment)
            } catch (e: Exception) {
                ResponseResult.Error(SOMETHING_WENT_WRONG)
            }
            emit(response)
        }




}


