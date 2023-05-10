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

class MainViewModel(val context: Context) : ViewModel() {

    var loading = MutableLiveData(View.GONE)
    var error = MutableLiveData("")
    private val _storyList = MutableLiveData<List<ListStoryItem>>()
    val storyList: LiveData<List<ListStoryItem>> = _storyList


    fun getStoryList(token: String) {
        loading.postValue(View.VISIBLE)
        val client = ApiConfig.getApiService().getListStory(token, 20)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) _storyList.postValue(response.body()?.listStory)
                else error.postValue("Error ${response.code()} : ${response.message()}")

                loading.postValue(View.GONE)
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                loading.postValue(View.GONE)
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue("${context.getString(R.string.error_fetch_data)} : ${t.message}")
            }
        })
    }

    companion object{
        const val TAG = "MainViewModel"
    }
}