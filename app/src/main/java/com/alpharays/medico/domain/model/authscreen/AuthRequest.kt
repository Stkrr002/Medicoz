package com.alpharays.medico.domain.model.authscreen

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    @SerializedName("contact") val contact: String? = null
)
