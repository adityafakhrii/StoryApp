package com.adityafakhri.storyapp.data.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adityafakhri.storyapp.R
import com.adityafakhri.storyapp.data.source.remote.response.ListStoryItem
import com.adityafakhri.storyapp.data.source.remote.response.StoryResponse
import com.adityafakhri.storyapp.data.source.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryMapsViewModel(val context: Context) : ViewModel() {

    private val _storyList = MutableLiveData<List<ListStoryItem>>()
    val storyList: LiveData<List<ListStoryItem>> = _storyList
//    val defaultLocation = MutableLiveData(Const.defaultLocation)

    val loading = MutableLiveData(View.GONE)
    val error = MutableLiveData("")
    val isError = MutableLiveData(true)

    fun loadStoryLocationData(token: String) {
        val client = ApiConfig.getApiService().getListStoryLocation(token, 100)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    isError.postValue(false)
                    _storyList.postValue(response.body()?.listStory)
                } else {
                    isError.postValue(true)
                    error.postValue("ERROR ${response.code()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                loading.postValue(View.GONE)
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue("${context.getString(R.string.error_fetch_data)} : ${t.message}")
            }
        })
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}