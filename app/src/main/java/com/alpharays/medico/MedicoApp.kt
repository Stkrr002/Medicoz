package com.alpharays.medico

import android.app.Application

/**
 * Injectors from different modules
**/
import com.alpharays.medico.data.di.MedicoInjector
import com.alpharays.medico.profile.data.di.MedicoProfileInjector
import com.alpharays.mymedicommfma.communityv2.community_app.data.di.MedicoCommunityInjector
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MedicoApp : Application() {
    private lateinit var medicoAppCompositionRoot: MedicoInjector
    private lateinit var communityAppCompositionRoot: MedicoCommunityInjector
    private lateinit var profileAppCompositionRoot: MedicoProfileInjector
    override fun onCreate() {
        super.onCreate()
        instance = this
        medicoAppCompositionRoot = MedicoInjector()
        communityAppCompositionRoot = MedicoCommunityInjector()
        profileAppCompositionRoot = MedicoProfileInjector()
    }

    fun getMedicoInjector(): MedicoInjector {
        return medicoAppCompositionRoot
    }

    fun getProfileInjector(): MedicoProfileInjector {
        return profileAppCompositionRoot
    }

    fun getCommunityInjector(): MedicoCommunityInjector {
        return communityAppCompositionRoot
    }

    companion object {
        private lateinit var instance: MedicoApp

        @JvmStatic
        fun getInstance(): MedicoApp {
            return instance
        }
    }
}