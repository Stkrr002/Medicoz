package com.alpharays.medico.profile.domain.usecase

import com.alpharays.alaskagemsdk.network.ResponseResult
import com.alpharays.medico.profile.domain.model.profilescreen.Profile
import com.alpharays.medico.profile.domain.repository.ProfileRepository
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.SOMETHING_WENT_WRONG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.alpharays.medico.profile.data.source.room.MedicoPatientProfileTable as PatientTable
import com.alpharays.medico.profile.domain.model.profilescreen.userposts.UserCommunityPostsParent as PostsResponse

class ProfileScreenUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    fun getProfile(token: String): Flow<ResponseResult<Profile>> = flow {
        emit(ResponseResult.Loading())
        val response = try {
            val profileInfo = profileRepository.getProfileInfo(token)
            profileInfo
        } catch (e: Exception) {
            ResponseResult.Error(SOMETHING_WENT_WRONG)
        }
        emit(response)
    }

    fun updateProfile(profileInfo: Profile, token: String): Flow<ResponseResult<Profile>> = flow {
        val response = try {
            val updatedProfileInfo = profileRepository.updateProfileInfo(profileInfo, token)
            updatedProfileInfo
        } catch (e: Exception) {
            ResponseResult.Error(SOMETHING_WENT_WRONG)
        }
        emit(response)
    }

    fun getMyPosts(docId: String): Flow<ResponseResult<PostsResponse>> = flow {
        emit(ResponseResult.Loading())
        val response = try {
            val myPosts = profileRepository.getMyCommunityPosts(docId)
            myPosts
        } catch (e: Exception) {
            ResponseResult.Error(SOMETHING_WENT_WRONG)
        }
        emit(response)
    }


    // cached data
    fun setCachedProfile(patientTable: PatientTable): Flow<Any> = flow {
        try {
            profileRepository.setCachedProfile(patientTable)
        } catch (e: Exception) {
            println(e.printStackTrace())
        }
    }

    fun getCachedProfile(): Flow<ProfileCachedData> = flow {
        val response = try {
            val response = ProfileCachedData(data = profileRepository.getCachedProfile())
            response
        } catch (e: Exception) {
            val response = ProfileCachedData(error = e.message)
            response
        }
        emit(response)
    }

    suspend fun getCurrentCachedProfile(): PatientTable? {
        return profileRepository.getCachedProfile()
    }
}

data class ProfileCachedData(
    val data: PatientTable? = null,
    val error: String? = null,
)