package com.gitfinder

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @Headers("Authorization: token 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")
    @GET("search/users?q=thoriqzs")
//    fun searchUser(@Query("q") q: String) : Call<SearchResponse>
    fun searchUser() : Call<SearchResponse>
}
