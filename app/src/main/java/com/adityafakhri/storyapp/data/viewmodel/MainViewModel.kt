package com.adityafakhri.storyapp.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.adityafakhri.storyapp.data.source.remote.response.ListStoryItem
import com.adityafakhri.storyapp.data.source.remote.room.StoryRepository

class MainViewModel(storyRepository: StoryRepository) : ViewModel() {
    val storyList: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStory().cachedIn(viewModelScope)
}