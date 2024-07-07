package com.alpharays.medico.profile.v2.di


import com.alpharays.medico.profile.v2.modules.profile.ProfileFeatureApi




object DependencyProviderProfile {

    private lateinit var profileFeatureApi: ProfileFeatureApi

    fun provideImpl(
        profileFeatureApi: ProfileFeatureApi,
    ) {
        DependencyProviderProfile.profileFeatureApi = profileFeatureApi
    }

    fun profileFeature(): ProfileFeatureApi = profileFeatureApi


}