package com.gitfinder

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("search/users")
    fun searchUser(@Query("q") q: String) : Call<SearchResponse>

    @GET("users/{username}")
    fun getUserDetail(@Path("username") username: String): Call<UserDetailResponse>
}
