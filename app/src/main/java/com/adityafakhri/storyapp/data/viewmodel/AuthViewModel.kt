package com.adityafakhri.storyapp.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.adityafakhri.storyapp.utils.Const
import com.adityafakhri.storyapp.data.source.local.AuthPreferences
import kotlinx.coroutines.launch

class AuthViewModel(private val pref: AuthPreferences): ViewModel() {
    fun getUserPreferences(property:String): LiveData<String> {
        return when(property){
            Const.UserPreferences.Token.name -> pref.getToken().asLiveData()
            else -> pref.getToken().asLiveData()
        }
    }

    fun setUserPreferences(userToken: String) {
        viewModelScope.launch {
            pref.saveLoginSession(userToken)
        }
    }

    fun clearUserPreferences() {
        viewModelScope.launch {
            pref.clearSession()
        }
    }
}