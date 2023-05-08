package com.adityafakhri.storyapp.ui.register

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adityafakhri.storyapp.data.source.remote.response.RegisterResponse
import com.adityafakhri.storyapp.data.source.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(val context: Context) : ViewModel() {

    var loading = MutableLiveData(View.GONE)
    val error = MutableLiveData("")

    val registerResult = MutableLiveData<RegisterResponse>()

    private val TAG = RegisterViewModel::class.simpleName

    fun register(name: String, email: String, password: String) {
        loading.postValue(View.VISIBLE)
        val client = ApiConfig.getApiService().userRegister(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.code() == 201) registerResult.postValue(response.body()) else error.postValue("ERROR ${response.code()} : ${response.message()}")
                loading.postValue(View.GONE)
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                loading.postValue(View.GONE)
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue(t.message)
            }
        })
    }
}