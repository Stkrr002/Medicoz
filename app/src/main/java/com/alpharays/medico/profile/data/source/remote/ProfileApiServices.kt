package com.alpharays.medico.profile.data.source.remote

import com.alpharays.medico.profile.domain.model.profilescreen.Profile
import com.alpharays.medico.profile.domain.model.profilescreen.userposts.UserCommunityPostsParent
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.CURR_DOCS_POSTS
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.DOC_ID_KEYWORD
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.GET_PROFILE
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.TOKEN_KEYWORD
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.UPDATE_PROFILE
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

interface ProfileApiServices {
    /**
     * profile screen endpoints
     */
    @GET(GET_PROFILE)
    suspend fun getProfileInfo(
        @Header(TOKEN_KEYWORD) token: String,
    ): Response<Profile>

    @PUT(UPDATE_PROFILE)
    suspend fun updateProfileInfo(
        @Body updateProfileInfo: Profile,
        @Header(TOKEN_KEYWORD) token: String,
    ): Response<Profile>

    @GET(CURR_DOCS_POSTS)
    suspend fun getMyAllPosts(
        @Header(DOC_ID_KEYWORD) docId: String,
    ): Response<UserCommunityPostsParent>
}