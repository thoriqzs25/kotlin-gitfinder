package com.gitfinder

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {

    private val _users = MutableLiveData<List<UserItem?>>()
    val users :LiveData<List<UserItem?>> = _users

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "mainviewmodel"
    }

    init {
        searchUsers("thoriqzs25")
    }

     fun searchUsers(q: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().searchUser(q)
        client.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _users.value = responseBody.items
                    }
                }
                else {
                    Log.d(TAG, "onResponseFail: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                _isLoading.value = false
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }
}