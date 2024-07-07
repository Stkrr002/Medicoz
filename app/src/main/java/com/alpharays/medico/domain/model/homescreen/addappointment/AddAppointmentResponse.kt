package com.alpharays.medico.domain.model.homescreen.addappointment


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class AddAppointmentResponse(
    @SerializedName("data") val newAppointmentResponse: String? = null,
    @SerializedName("error") val error: String? = null,
    @SerializedName("errorlist") val errorList: List<String>? = null,
    @SerializedName("success") val success: String? = null,
)