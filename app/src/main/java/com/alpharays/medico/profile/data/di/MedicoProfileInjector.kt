package com.alpharays.medico.profile.data.di

import android.content.Context
import com.alpharays.alaskagemsdk.network.ResponseHandler
import com.alpharays.medico.MedicoApp
import com.alpharays.medico.profile.data.source.remote.ProfileApiServices
import com.alpharays.medico.profile.data.source.repo_impl.ProfileRepositoryImpl
import com.alpharays.medico.profile.domain.usecase.ProfileScreenUseCase
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.API_SAFE_KEY
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.API_SAFE_KEY_VALUE
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.BASE_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MedicoProfileInjector {

    /**
     * Base url & api services declaration
     */
    private val baseUrl = BASE_URL
    private lateinit var profileApiServices: ProfileApiServices
    private lateinit var context: Context

    /**
     * api services initialization with OkHttpClient and retrofit
     */
    @Synchronized
    fun getProfileApiServices(): ProfileApiServices {
        if (!::profileApiServices.isInitialized) {

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

            profileApiServices = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(
                    GsonConverterFactory.create(getGson())
                )
                .build()
                .create(ProfileApiServices::class.java)
        }
        return profileApiServices
    }


    /**
     * Custom GSon initialization with leniency
     */
    private fun getGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }


    /**
     * Response Handler initialization
     */
    private lateinit var responseHandler: ResponseHandler
    private fun getResponseHandler(): ResponseHandler {
        if (!::responseHandler.isInitialized) {
            responseHandler = ResponseHandler()
        }
        return ResponseHandler()
    }



    /**
     * Profile Screen and its useCase and RepositoryImpl
     */
    private lateinit var profileUseCase: ProfileScreenUseCase
    fun getProfileUseCase(): ProfileScreenUseCase {
        if (!::profileUseCase.isInitialized) {
            val apiServices = getProfileApiServices()
            val responseHandler = getResponseHandler()
            val impl = ProfileRepositoryImpl(apiServices, responseHandler)
            profileUseCase = ProfileScreenUseCase(impl)
        }
        return profileUseCase
    }
}