package com.adityafakhri.storyapp.data.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adityafakhri.storyapp.data.source.remote.response.LoginResponse
import com.adityafakhri.storyapp.data.source.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(val context: Context) : ViewModel() {

    var loading = MutableLiveData(View.GONE)
    val error = MutableLiveData("")
    val loginResult = MutableLiveData<LoginResponse>()

    fun checkLogin(email: String, password: String) {
        loading.postValue(View.VISIBLE)
        val client = ApiConfig.getApiService().userLogin(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.code() == 200) loginResult.postValue(response.body())
                else error.postValue("ERROR ${response.code()} : ${response.message()}")
                loading.postValue(View.GONE)
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loading.postValue(View.GONE)
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue(t.message)
            }
        })
    }

    companion object {
        const val TAG = "LoginViewModel"
    }
}