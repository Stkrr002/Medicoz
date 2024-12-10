package com.alpharays.medico.profile.data.di

import android.content.Context
import com.alpharays.alaskagemsdk.network.ResponseHandler
import com.alpharays.medico.profile.data.source.remote.ProfileApiServices
import com.alpharays.medico.profile.data.source.repo_impl.ProfileRepositoryImpl
import com.alpharays.medico.profile.domain.repository.ProfileRepository
import com.alpharays.mymedicommfma.common.connectivity.NetworkConnectivityObserver
import com.alpharays.mymedicommfma.communityv2.MedCommRouter.API_SAFE_KEY
import com.alpharays.mymedicommfma.communityv2.MedCommRouter.API_SAFE_KEY_VALUE
import com.alpharays.mymedicommfma.communityv2.MedCommRouter.BASE_URL
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileAppModule {
    @Provides
    @Singleton
    fun provideHttpClient(): ProfileApiServices {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        // Create an OkHttpClient with an interceptor to add the token header
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val newRequest = originalRequest.newBuilder()
                    .header(
                        API_SAFE_KEY,
                        API_SAFE_KEY_VALUE
                    )
                    .build()
                chain.proceed(newRequest)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setLenient().setPrettyPrinting().create()
                )
            )
            .build()
            .create(ProfileApiServices::class.java)
    }


}