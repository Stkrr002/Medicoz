package com.alpharays.medico.data.source.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MedicoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAppointmentList(appointments: MedicoAppointmentTable): Long

    @Query("SELECT * FROM medico_appointments_table")
    suspend fun getAppointmentList(): MedicoAppointmentTable
}