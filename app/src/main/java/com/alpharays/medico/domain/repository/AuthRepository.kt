package com.alpharays.medico.domain.repository

import com.alpharays.alaskagemsdk.network.ResponseResult
import com.alpharays.medico.domain.model.authscreen.AuthResponseBody

interface AuthRepository {
    suspend fun getAuthTokenInfo(contactNumber: String): ResponseResult<AuthResponseBody>
}