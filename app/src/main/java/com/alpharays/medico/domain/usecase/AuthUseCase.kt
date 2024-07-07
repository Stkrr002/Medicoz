package com.alpharays.medico.domain.usecase

import com.alpharays.alaskagemsdk.network.ResponseResult
import com.alpharays.medico.domain.model.authscreen.AuthResponseBody
import com.alpharays.medico.domain.repository.AuthRepository
import com.alpharays.medico.medico_utils.MedicoConstants.SOMETHING_WENT_WRONG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(contactNumber: String): Flow<ResponseResult<AuthResponseBody>> = flow {
        emit(ResponseResult.Loading())
        val response = try {
            val authInfo = authRepository.getAuthTokenInfo(contactNumber)
            authInfo
        } catch (e: Exception) {
            ResponseResult.Error(SOMETHING_WENT_WRONG)
        }
        emit(response)
    }
}