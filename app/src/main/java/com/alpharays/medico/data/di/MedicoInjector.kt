package com.alpharays.medico.data.di

import android.content.Context
import com.alpharays.alaskagemsdk.network.ResponseHandler
import com.alpharays.medico.data.source.remote.ApiServices
import com.alpharays.medico.data.source.repo_impl.AuthRepositoryImpl
import com.alpharays.medico.data.source.repo_impl.HomeRepositoryImpl
import com.alpharays.medico.domain.usecase.AuthUseCase
import com.alpharays.medico.domain.usecase.HomeScreenUseCase
import com.alpharays.medico.medico_utils.MedicoConstants.API_SAFE_KEY
import com.alpharays.medico.medico_utils.MedicoConstants.API_SAFE_KEY_VALUE
import com.alpharays.medico.medico_utils.MedicoConstants.BASE_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MedicoInjector {

    /**
     * Base url & api services declaration
     */
    private val baseUrl = BASE_URL
    private lateinit var apiServices: ApiServices
    private lateinit var context: Context

    /**
     * api services initialization with OkHttpClient and retrofit
     */
    @Synchronized
    fun getApiServices(): ApiServices {
        if (!::apiServices.isInitialized) {

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

            apiServices = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(
                    GsonConverterFactory.create(getGson())
                )
                .build()
                .create(ApiServices::class.java)
        }
        return apiServices
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
     * Auth Screen and its useCase and RepositoryImpl
     */
    private lateinit var authUseCase: AuthUseCase
    fun getAuthUseCase(): AuthUseCase {
        if (!::authUseCase.isInitialized) {
            val apiServices = getApiServices()
            val responseHandler = getResponseHandler()
            val impl = AuthRepositoryImpl(apiServices, responseHandler)
            authUseCase = AuthUseCase(impl)
        }
        return authUseCase
    }


    /**
     * Home appointment Screen and its useCase and RepositoryImpl
     */
    private lateinit var homeUseCase: HomeScreenUseCase
    fun getHomeUseCase(): HomeScreenUseCase {
        if (!::homeUseCase.isInitialized) {
            val apiServices = getApiServices()
            val responseHandler = getResponseHandler()
            val impl = HomeRepositoryImpl(apiServices, responseHandler)
            homeUseCase = HomeScreenUseCase(impl)
        }
        return homeUseCase
    }
}