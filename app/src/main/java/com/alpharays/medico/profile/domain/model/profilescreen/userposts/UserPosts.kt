package com.alpharays.medico.profile.domain.model.profilescreen.userposts


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

// img url
// time of posting
@Serializable
data class UserPosts(
    @SerializedName("_id") val id: String? = null,
    val posterId: String? = null,
    val posterName: String? = null,
    val postTitle: String? = null,
    val postContent: String? = null,
    val reactions: Reactions? = null,
    val comments: List<String>? = null,
    @SerializedName("__v") val v: Int,
)

@Serializable
data class Reactions(
    val celebrate: List<ReactionDetails>? = null,
    val funny: List<ReactionDetails>? = null,
    val insightful: List<ReactionDetails>? = null,
    val like: List<ReactionDetails>? = null,
    val love: List<ReactionDetails>? = null,
    val support: List<ReactionDetails>? = null,
)

@Serializable
data class ReactionDetails(
    val userId: String? = null,
    val userName: String? = null,
)