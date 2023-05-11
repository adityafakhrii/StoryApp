package com.adityafakhri.storyapp.data.source.remote.room

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.adityafakhri.storyapp.data.database.StoryDatabase
import com.adityafakhri.storyapp.data.source.remote.remotemediator.StoryRemoteMediator
import com.adityafakhri.storyapp.data.source.remote.response.ListStoryItem
import com.adityafakhri.storyapp.data.source.remote.retrofit.ApiService

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService, private val token:String) {
    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(
                storyDatabase,
                apiService,
                token
            ),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}