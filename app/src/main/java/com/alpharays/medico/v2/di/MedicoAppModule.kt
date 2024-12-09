package com.alpharays.medico.v2.di

import android.content.Context
import com.alpharays.alaskagemsdk.network.ResponseHandler
import com.alpharays.medico.data.source.remote.ApiServices
import com.alpharays.medico.data.source.repo_impl.AuthRepositoryImpl
import com.alpharays.medico.data.source.repo_impl.HomeRepositoryImpl
import com.alpharays.medico.domain.repository.AuthRepository
import com.alpharays.medico.domain.repository.HomeRepository
import com.alpharays.medico.domain.usecase.AuthUseCase
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
object MedicoAppModule {
    @Provides
    @Singleton
    fun provideHttpClient(): ApiServices {
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
            .create(ApiServices::class.java)
    }


    @Provides
    @Singleton
    fun provideAuthRepository(
        apiServices: ApiServices,
        responseHandler: ResponseHandler,
    ): AuthRepository {
        return AuthRepositoryImpl(apiServices, responseHandler)
    }



    @Provides
    @Singleton
    fun provideHomeRepository(
        apiServices: ApiServices,
        responseHandler: ResponseHandler
    ): HomeRepository {
        return HomeRepositoryImpl(apiServices, responseHandler)
    }

    @Provides
    @Singleton
    fun provideAuthUseCase(authRepository: AuthRepository): AuthUseCase {
        return AuthUseCase(authRepository)
    }

}