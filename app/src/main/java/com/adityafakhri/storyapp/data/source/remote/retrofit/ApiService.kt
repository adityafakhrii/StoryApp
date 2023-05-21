package com.adityafakhri.storyapp.data.source.remote.retrofit

import com.adityafakhri.storyapp.data.source.remote.response.AddStoryResponse
import com.adityafakhri.storyapp.data.source.remote.response.LoginResponse
import com.adityafakhri.storyapp.data.source.remote.response.RegisterResponse
import com.adityafakhri.storyapp.data.source.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun userRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @GET("stories")
    suspend fun getListStory(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryResponse

    @GET("stories?location=1")
    fun getListStoryLocation(
        @Header("Authorization") token: String,
        @Query("size") size: Int
    ): Call<StoryResponse>

    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Call<AddStoryResponse>
}