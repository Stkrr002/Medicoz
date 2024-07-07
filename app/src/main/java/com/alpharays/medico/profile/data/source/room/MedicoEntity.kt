package com.alpharays.medico.profile.data.source.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.alpharays.medico.profile.data.source.room.typeconverters.ProfileConverter
import com.alpharays.medico.profile.domain.model.profilescreen.Profile
import com.alpharays.medico.profile.domain.model.profilescreen.userposts.UserPosts
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.MEDICO_PROFILE_TABLE


@Entity(tableName = MEDICO_PROFILE_TABLE)
@TypeConverters(ProfileConverter::class)
data class MedicoPatientProfileTable(
    @PrimaryKey val id: Int = 0,
    val profile: Profile? = null,
    val posts: List<UserPosts> = emptyList(),
)