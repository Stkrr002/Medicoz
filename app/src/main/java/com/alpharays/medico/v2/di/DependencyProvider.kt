package com.alpharays.medico.v2.di

import com.alpharays.medico.v2.modules.auth.AuthFeatureApi
import com.alpharays.medico.v2.modules.home.HomeFeatureApi
import com.alpharays.mymedicommfma.communityv2.CommunityFeatureApi
import com.alpharays.mymedjarvisfma.integration.MedJarvisFeatureApi


// todo add any other module screen here  replace with dependency injection


object DependencyProvider {

    /* Don't use lateinit in real project :) */
    private lateinit var homeFeatureApi: HomeFeatureApi
    private lateinit var communityFeatureApi: CommunityFeatureApi
    private lateinit var authFeatureApi: AuthFeatureApi
    private lateinit var medJarvisFeatureApi: MedJarvisFeatureApi


    fun provideImpl(
        homeFeatureApi: HomeFeatureApi,
        communityFeatureApi: CommunityFeatureApi,
        authFeatureApi: AuthFeatureApi,
        medJarvisFeatureApi: MedJarvisFeatureApi
    ) {
        this.homeFeatureApi = homeFeatureApi
        this.communityFeatureApi = communityFeatureApi
        this.authFeatureApi = authFeatureApi
        this.medJarvisFeatureApi = medJarvisFeatureApi
    }

    fun homeFeature(): HomeFeatureApi = homeFeatureApi

    fun communityFeature(): CommunityFeatureApi = communityFeatureApi

    fun authFeature(): AuthFeatureApi = authFeatureApi

    fun medJarvisFeature(): MedJarvisFeatureApi = medJarvisFeatureApi

}