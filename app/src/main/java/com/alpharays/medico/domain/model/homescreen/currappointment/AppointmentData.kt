package com.alpharays.medico.domain.model.homescreen.currappointment

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class AppointmentData(
    @SerializedName("appointmentList")
    val appointmentList: List<Appointment>? = null
)