package com.alpharays.medico.data.source.room.typeconverters

import androidx.room.TypeConverter
import com.alpharays.medico.domain.model.homescreen.currappointment.Appointment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AppointmentListConverter {
    @TypeConverter
    fun fromJson(json: String?): List<Appointment>? {
        return json?.let {
            val type = object : TypeToken<List<Appointment>>() {}.type
            Gson().fromJson(it, type)
        }
    }

    @TypeConverter
    fun toJson(appointments: List<Appointment>?): String? {
        return appointments?.let {
            Gson().toJson(it)
        }
    }
}