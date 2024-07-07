package com.alpharays.medico.profile.domain.model.profilescreen.userposts

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UserCommunityPostsParent(
    @SerializedName("success") val success: Int? = null,
    @SerializedName("error") val error: String? = null,
    @SerializedName("errorlist") val errorList: List<String>? = null,
    @SerializedName("data") val myPostsData: List<UserPosts>? = null,
)