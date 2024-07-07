package com.alpharays.medico.domain.model.homescreen.addappointment


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class AddAppointmentData(
    @SerializedName("appointmentStatus")
    val appointmentStatus: String? = null,
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("oldNewStatus")
    val oldNewStatus: String? = null,
    @SerializedName("patientDisease")
    val patientDisease: String? = null,
    @SerializedName("patientName")
    val patientName: String? = null,
    @SerializedName("time")
    val time: String? = null,
)