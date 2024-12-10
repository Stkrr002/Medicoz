package com.alpharays.medico.profile.domain.repository

import com.alpharays.alaskagemsdk.network.ResponseResult
import com.alpharays.medico.profile.domain.model.profilescreen.Profile
import com.alpharays.medico.profile.domain.model.profilescreen.userposts.UserCommunityPostsParent

interface ProfileRepository {
    suspend fun getProfileInfo(token: String): ResponseResult<Profile>
    suspend fun updateProfileInfo(profileInfo: Profile, token: String): ResponseResult<Profile>
    suspend fun getMyCommunityPosts(docId: String): ResponseResult<UserCommunityPostsParent>
}