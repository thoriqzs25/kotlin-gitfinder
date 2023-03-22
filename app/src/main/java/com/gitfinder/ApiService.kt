package com.gitfinder

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @Headers("Authorization: token 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")
    @GET("search/users")
    fun searchUser(@Query("q") q: String) : Call<SearchResponse>
}