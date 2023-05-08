package com.adityafakhri.storyapp.utils

object Const {

    enum class UserPreferences {
        Token, Name, Email
    }

    enum class StoryDetail {
        UserName, ImageURL, ContentDescription
    }

    const val preferenceName = "Settings"
    const val preferenceDefaultValue = "Not Set"
    val emailFormat = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
}