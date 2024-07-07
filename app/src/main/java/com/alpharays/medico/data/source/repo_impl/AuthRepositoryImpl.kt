package com.alpharays.medico.data.source.repo_impl

import com.alpharays.alaskagemsdk.network.ResponseHandler
import com.alpharays.alaskagemsdk.network.ResponseResult
import com.alpharays.medico.data.source.remote.ApiServices
import com.alpharays.medico.domain.model.authscreen.AuthRequest
import com.alpharays.medico.domain.model.authscreen.AuthResponseBody
import com.alpharays.medico.domain.repository.AuthRepository
import com.alpharays.medico.medico_utils.MedicoConstants.SOMETHING_WENT_WRONG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiServices: ApiServices,
    private val responseHandler: ResponseHandler,
) : AuthRepository {
    override suspend fun getAuthTokenInfo(contactNumber: String): ResponseResult<AuthResponseBody> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                responseHandler.callAPI {
                    withContext(Dispatchers.IO) {
                        val authRequest = AuthRequest(contactNumber)
                        Response.success(apiServices.getAuthTokenInfo(authRequest).body())
                    }
                }
            } catch (e: Exception) {
                ResponseResult.Error(SOMETHING_WENT_WRONG)
            }
        }
}