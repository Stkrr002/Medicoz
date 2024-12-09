package com.alpharays.medico.data.source.repo_impl

import com.alpharays.alaskagemsdk.network.ResponseHandler
import com.alpharays.alaskagemsdk.network.ResponseResult
import com.alpharays.medico.data.source.remote.ApiServices
import com.alpharays.medico.domain.model.homescreen.currappointment.Appointment
import com.alpharays.medico.domain.model.homescreen.currappointment.AppointmentDetailsResponse
import com.alpharays.medico.domain.repository.HomeRepository
import com.alpharays.medico.medico_utils.MedicoConstants.SOMETHING_WENT_WRONG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class HomeRepositoryImpl(
    private val apiServices: ApiServices,
    private val responseHandler: ResponseHandler,
) : HomeRepository {
    override suspend fun getAppointmentList(token: String): ResponseResult<AppointmentDetailsResponse> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                responseHandler.callAPI {
                    withContext(Dispatchers.IO) {
                        Response.success(apiServices.getAppointmentList(token).body())
                    }
                }
            } catch (e: Exception) {
                ResponseResult.Error(SOMETHING_WENT_WRONG)
            }
        }


    override suspend fun addNewAppointment(
        docId: String,
        newAppointment: Appointment
    ): ResponseResult<Any> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                responseHandler.callAPI {
                    withContext(Dispatchers.IO) {
                        apiServices.addAppointment(docId, newAppointment)
                    }
                }
            } catch (e: Exception) {
                ResponseResult.Error(SOMETHING_WENT_WRONG)
            }
        }
}