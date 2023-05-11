package com.adityafakhri.storyapp.utils

import com.google.android.gms.maps.model.LatLng

object Const {

    enum class UserPreferences {
        Token
    }

    enum class StoryDetail {
        UserName, ImageURL, ContentDescription
    }

    const val preferenceName = "Settings"
    const val preferenceDefaultValue = "Not Set"
    val emailFormat = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")

    val defaultLocation = LatLng(-2.3932789, 108.8507139)
}