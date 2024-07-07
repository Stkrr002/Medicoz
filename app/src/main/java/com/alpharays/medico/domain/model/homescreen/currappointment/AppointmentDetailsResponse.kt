package com.alpharays.medico.domain.model.homescreen.currappointment

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class AppointmentDetailsResponse(
    @SerializedName("success")
    val success: Int? = null,
    @SerializedName("error")
    val error: String? = null,
    @SerializedName("errorlist")
    val errorList: List<String>? = null,
    @SerializedName("data")
    val data: List<AppointmentData>? = null
)