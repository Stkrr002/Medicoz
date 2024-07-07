package com.alpharays.medico.domain.model.homescreen.currappointment

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Appointment(
    @SerializedName("patientName")
    val patientName: String? = null,
    @SerializedName("patientDisease")
    val patientDisease: String? = null,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("time")
    val time: String? = null,
    @SerializedName("oldNewStatus")
    val oldNewStatus: String? = null,
    @SerializedName("appointmentStatus")
    val appointmentStatus: String? = null
)