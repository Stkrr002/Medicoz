package com.alpharays.medico.profile.domain.model.profilescreen

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val name: String? = null,
    val department: String? = null,
    val age: String? = null,
    val gender: String? = null,
    val yearsOfExp: String? = null,
    val qualifications: String? = null,
    val avatarImageUrl: String? = null,
    val phone: String? = null,
    val email: String? = null
)