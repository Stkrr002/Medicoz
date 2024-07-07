package com.alpharays.medico.profile.presentation.profile_screen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpharays.alaskagemsdk.network.ResponseResult
import com.alpharays.medico.profile.data.source.room.MedicoPatientProfileTable
import com.alpharays.medico.profile.domain.model.profilescreen.Profile
import com.alpharays.medico.profile.domain.model.profilescreen.userposts.UserCommunityPostsParent
import com.alpharays.medico.profile.domain.usecase.ProfileScreenUseCase
import com.alpharays.medico.profile.profile_utils.connectivity.ConnectivityObserver
import com.alpharays.medico.profile.profile_utils.util.MedicoUtils
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.UNEXPECTED_ERROR
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val profileScreenUseCase: ProfileScreenUseCase
) : ViewModel() {
    private var token = ""
    private var docId = ""
    private val _remoteProfileInfoState = MutableStateFlow(ProfileState())
    val remoteProfileInfoStateFlow: StateFlow<ProfileState> = _remoteProfileInfoState.asStateFlow()

    private val _remoteProfilePostsState = MutableStateFlow(ProfilePostsState())
    val remoteProfilePostsStateFlow: StateFlow<ProfilePostsState> = _remoteProfilePostsState.asStateFlow()


    private val _cachedProfileInfoState = MutableStateFlow(ProfileState())
    val cachedProfileInfoStateFlow: StateFlow<ProfileState> = _cachedProfileInfoState.asStateFlow()

    private val _cachedProfilePostsState = MutableStateFlow(ProfilePostsState())
    val cachedProfilePostsStateFlow: StateFlow<ProfilePostsState> = _cachedProfilePostsState.asStateFlow()


    private val _getCurrentCachedProfile = MutableStateFlow(MedicoPatientProfileTable())
    val getCurrentCachedProfile: StateFlow<MedicoPatientProfileTable> = _getCurrentCachedProfile.asStateFlow()

    private val _networkStatus = MutableStateFlow(ConnectivityObserver.Status.Lost)
    val networkStatus: StateFlow<ConnectivityObserver.Status> = _networkStatus

    private var remoteCallCount = 0


    val combinedProfileInfoData = _cachedProfileInfoState.combine(_remoteProfileInfoState){ cachedData, remoteData ->
        println("cached Data : B1 : $cachedData")
        println("remote Data : B2 : $remoteData")

        val remoteProfileData = remoteData.profileInfo

        if(remoteProfileData != null){
            cachedData.copy(
                isLoading = remoteData.isLoading,
                profileInfo = remoteProfileData,
                error = remoteData.error
            )
        }
        else{
            cachedData
        }
    }
    .onEach { _cachedProfileInfoState.emit(it) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis = 3000), _cachedProfileInfoState.value)


    val combinedProfilePostsData = _cachedProfilePostsState.combine(_remoteProfilePostsState) { cachedData, remoteData ->
        val remoteProfilePostData = remoteData.profilePosts
        remoteProfilePostData?.myPostsData?.let { _ ->
            cachedData.copy(
                isLoading = remoteData.isLoading,
                profilePosts = UserCommunityPostsParent(
                    success = remoteProfilePostData.success,
                    error = remoteProfilePostData.error,
                    errorList = remoteProfilePostData.errorList,
                    myPostsData = remoteProfilePostData.myPostsData
                ),
                error = remoteData.error
            )
        } ?: cachedData
    }
    .onEach { _cachedProfilePostsState.emit(it) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis = 3000), _cachedProfilePostsState.value)

    init {
        token = MedicoUtils.getAuthToken()
        docId = MedicoUtils.getDocId()
        getCachedProfileInfo()
    }

    fun updateNetworkStatus(status: ConnectivityObserver.Status) {
        _networkStatus.value = status
        if(status == ConnectivityObserver.Status.Available && remoteCallCount == 0){
            /**
             * runs only once - when a screen is visible for the first time
             * and other times when the network status is changed,
             * retry remote functions are executed
             */
            remoteCallCount++
            getRemoteProfileInfo()
            getRemoteMyCommunityPosts()
        }
    }

    fun retryGettingProfileData(){
        getRemoteProfileInfo()
        getRemoteMyCommunityPosts()
    }

    private fun getRemoteProfileInfo() {
        profileScreenUseCase.getProfile(token).onEach { result ->
            when (result) {
                is ResponseResult.Loading -> {
                    _remoteProfileInfoState.value = ProfileState(isLoading = true)
                }

                is ResponseResult.Success -> {
                    val profile = result.data
                    getCurrentCachedProfile()
                    val currentProfile = _getCurrentCachedProfile.value
                    profile?.let {
                        val updatedProfile = currentProfile.copy(profile = profile)
                        setProfileInfoInCache(updatedProfile)
                    }
                    _remoteProfileInfoState.value = ProfileState(profileInfo = result.data)
                }

                is ResponseResult.Error -> {
                    _remoteProfileInfoState.value = ProfileState(error = result.message ?: UNEXPECTED_ERROR)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getRemoteMyCommunityPosts() {
        profileScreenUseCase.getMyPosts(docId).onEach { result ->
            when (result) {
                is ResponseResult.Loading -> {
                    _remoteProfilePostsState.value = ProfilePostsState(isLoading = true)
                }

                is ResponseResult.Success -> {
                    val posts = result.data?.myPostsData
                    getCurrentCachedProfile()
//                    val currentProfile = _getCurrentCachedProfile.value : TODO : when posts was being saved, profile became null & vice versa - fix it
//                    posts?.let {
//                        val updatedProfile = currentProfile.copy(posts = posts)
//                        setProfileInfoInCache(updatedProfile)
//                    }
                    _remoteProfilePostsState.value = ProfilePostsState(profilePosts = result.data)
                }

                is ResponseResult.Error -> {
                    _remoteProfilePostsState.value = ProfilePostsState(error = result.message ?: UNEXPECTED_ERROR)
                }
            }
        }.launchIn(viewModelScope)
    }


    //  ************   room db - cached data   ************
    private fun setProfileInfoInCache(medicoPatientProfileTable: MedicoPatientProfileTable){
        profileScreenUseCase.setCachedProfile(medicoPatientProfileTable).launchIn(viewModelScope)
    }

    private fun getCachedProfileInfo() {
        profileScreenUseCase.getCachedProfile().onEach{
            if(it.data != null){
                val profileTable = it.data
                if(profileTable.profile != null){
                    val profile = profileTable.profile
                    _cachedProfileInfoState.value = ProfileState(profileInfo = profile)
                }
                else{
                    _cachedProfileInfoState.value = _remoteProfileInfoState.value
                }

                if(profileTable.posts.isNotEmpty()){
                    val posts = profileTable.posts
                    val postData = UserCommunityPostsParent(myPostsData = posts)
                    _cachedProfilePostsState.value = ProfilePostsState(profilePosts = postData)
                }
                else{
                    _cachedProfilePostsState.value = _remoteProfilePostsState.value
                }
            }
            else{
                _cachedProfileInfoState.value = _remoteProfileInfoState.value
                _cachedProfilePostsState.value = _remoteProfilePostsState.value
            }
        }.launchIn(viewModelScope)
    }

    private fun getCurrentCachedProfile() {
        viewModelScope.launch {
            val response = profileScreenUseCase.getCurrentCachedProfile()
            response?.let {
                _getCurrentCachedProfile.value = response
            }
        }
    }
}

data class ProfileState(
    var isLoading: Boolean? = false,
    var profileInfo: Profile? = null,
    var error: String? = null
)

data class ProfilePostsState(
    var isLoading: Boolean? = false,
    var profilePosts: UserCommunityPostsParent? = null,
    var error: String? = null
)