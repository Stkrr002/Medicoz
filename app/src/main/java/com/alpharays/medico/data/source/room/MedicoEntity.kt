package com.alpharays.medico.data.source.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.alpharays.medico.data.source.room.typeconverters.AppointmentListConverter
import com.alpharays.medico.domain.model.homescreen.currappointment.Appointment
import com.alpharays.medico.medico_utils.MedicoConstants.MEDICO_APPOINTMENTS_TABLE

@Entity(tableName = MEDICO_APPOINTMENTS_TABLE)
@TypeConverters(AppointmentListConverter::class)
data class MedicoAppointmentTable(
    @PrimaryKey val id: Int = 0,
    val data: List<Appointment>,
)