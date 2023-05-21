package com.adityafakhri.storyapp.data.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adityafakhri.storyapp.data.database.StoryDatabase
import com.adityafakhri.storyapp.data.source.remote.retrofit.ApiService
import com.adityafakhri.storyapp.data.source.remote.room.StoryRepository

class ViewModelStoryFactory(
    val context: Context,
    private val apiService: ApiService,
    val token: String
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            val database = StoryDatabase.getDatabase(context)
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(
                StoryRepository(database, apiService, token)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}