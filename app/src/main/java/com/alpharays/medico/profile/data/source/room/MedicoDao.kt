package com.alpharays.medico.profile.data.source.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MedicoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setPatientProfile(profile: MedicoPatientProfileTable)

    @Query("SELECT * FROM medico_profile_table WHERE id = 0")
    suspend fun getPatientProfile(): MedicoPatientProfileTable

}