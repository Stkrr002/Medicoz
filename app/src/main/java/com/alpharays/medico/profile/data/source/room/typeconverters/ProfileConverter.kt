package com.alpharays.medico.profile.data.source.room.typeconverters

import androidx.room.TypeConverter
import com.alpharays.medico.profile.domain.model.profilescreen.Profile
import com.alpharays.medico.profile.domain.model.profilescreen.userposts.UserPosts

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProfileConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromProfileToJson(profile: Profile?): String {
        return gson.toJson(profile)
    }

    @TypeConverter
    fun fromJsonToProfile(json: String?): Profile? {
        return if (json == null) null else gson.fromJson(json, Profile::class.java)
    }

    @TypeConverter
    fun fromMyCommunityPostsListToJson(posts: List<UserPosts>?): String {
        return gson.toJson(posts)
    }

    @TypeConverter
    fun fromJsonToMyCommunityPostsList(json: String?): List<UserPosts>? {
        if (json == null) return null
        val type = object : TypeToken<List<UserPosts>>() {}.type
        return gson.fromJson(json, type)
    }
}
