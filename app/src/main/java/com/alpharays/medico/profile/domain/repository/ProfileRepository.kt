package com.alpharays.medico.profile.domain.repository

import com.alpharays.alaskagemsdk.network.ResponseResult
import com.alpharays.medico.profile.data.source.room.MedicoPatientProfileTable
import com.alpharays.medico.profile.domain.model.profilescreen.Profile
import com.alpharays.medico.profile.domain.model.profilescreen.userposts.UserCommunityPostsParent

interface ProfileRepository {
    suspend fun getProfileInfo(token: String): ResponseResult<Profile>
    suspend fun updateProfileInfo(profileInfo: Profile, token: String): ResponseResult<Profile>
    suspend fun getMyCommunityPosts(docId: String): ResponseResult<UserCommunityPostsParent>
    suspend fun setCachedProfile(profile: MedicoPatientProfileTable)
    suspend fun getCachedProfile(): MedicoPatientProfileTable?
}