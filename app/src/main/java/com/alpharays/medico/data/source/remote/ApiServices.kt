package com.alpharays.medico.data.source.remote

import com.alpharays.medico.domain.model.authscreen.AuthRequest
import com.alpharays.medico.domain.model.authscreen.AuthResponseBody
import com.alpharays.medico.domain.model.homescreen.currappointment.Appointment
import com.alpharays.medico.domain.model.homescreen.currappointment.AppointmentDetailsResponse
import com.alpharays.medico.medico_utils.MedicoConstants.ADD_NEW_APPOINTMENT
import com.alpharays.medico.medico_utils.MedicoConstants.AUTH_BYPASS
import com.alpharays.medico.medico_utils.MedicoConstants.DOC_ID_KEYWORD
import com.alpharays.medico.medico_utils.MedicoConstants.GET_ALL_APPOINTMENTS
import com.alpharays.medico.medico_utils.MedicoConstants.TOKEN_KEYWORD
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiServices {
    /**
     * auth screen endpoints
     */
    @POST(AUTH_BYPASS)
    suspend fun getAuthTokenInfo(@Body authRequest: AuthRequest): Response<AuthResponseBody>


    /**
     * home screen endpoints
     */
    @GET(GET_ALL_APPOINTMENTS)
    suspend fun getAppointmentList(@Header(TOKEN_KEYWORD) token: String): Response<AppointmentDetailsResponse>

    @POST(ADD_NEW_APPOINTMENT)
    suspend fun addAppointment(
        @Path(DOC_ID_KEYWORD) docId: String,
        @Body appointmentInfo: Appointment,
    ): Response<Any>
}