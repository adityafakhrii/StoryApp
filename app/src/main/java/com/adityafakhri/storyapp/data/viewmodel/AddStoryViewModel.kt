package com.adityafakhri.storyapp.data.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adityafakhri.storyapp.R
import com.adityafakhri.storyapp.data.source.remote.response.AddStoryResponse
import com.adityafakhri.storyapp.data.source.remote.retrofit.ApiConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModel(val context: Context) : ViewModel() {

    var loading = MutableLiveData(View.GONE)
    var error = MutableLiveData("")
    var isSuccessUpload = MutableLiveData(false)

    fun uploadNewStory(
        token: String,
        file: File,
        description: String,
        lat: RequestBody?,
        lon: RequestBody?
    ) {
        loading.postValue(View.VISIBLE)

        val desc = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )

        val client = ApiConfig.getApiService().addStory(token, imageMultipart, desc, lat, lon)
        client.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                if (response.isSuccessful) {
                    isSuccessUpload.postValue(true)
                } else {
                    error.postValue("Error ${response.code()} : ${response.message()}")
                }
                loading.postValue(View.GONE)
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                loading.postValue(View.GONE)
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue("${context.getString(R.string.error_upload)} : ${t.message}")
            }
        })
    }

    companion object {
        const val TAG = "AddStoryViewModel"
    }
}