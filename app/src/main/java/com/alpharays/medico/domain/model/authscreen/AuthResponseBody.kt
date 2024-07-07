package com.alpharays.medico.domain.model.authscreen

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@Serializable
data class AuthResponseBody(
    @SerializedName("success") val success: Int? = null,
    @SerializedName("error") val error: String? = null,
    @SerializedName("errorlist") val errorList: List<String>? = null,
    @SerializedName("data") val data: AuthResponse? = null,
)

@Serializable
data class AuthResponse(
    val token: String? = null,
)