package com.alpharays.medico.data.source.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alpharays.medico.medico_utils.MedicoConstants.MEDICO_DATABASE

@Database(
    entities = [MedicoAppointmentTable::class], version = 1
)
abstract class MedicoDatabase : RoomDatabase() {
    abstract fun medicoDao(): MedicoDao

    companion object {
        @Volatile
        private var INSTANCE: MedicoDatabase? = null

        fun getDatabase(context: Context): MedicoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MedicoDatabase::class.java,
                    MEDICO_DATABASE
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}