package com.gitfinder

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userDetail = MutableLiveData<UserDetailResponse>()
    val userDetail: LiveData<UserDetailResponse> = _userDetail

    private val _followList = MutableLiveData<List<FollowResponseItem?>>()
    val followList: LiveData<List<FollowResponseItem?>> = _followList

    private val _errorMsg = MutableLiveData<Event<String>>()
    val errorMsg: LiveData<Event<String>> = _errorMsg

    companion object {
        private const val TAG = "detailviewmodelthoriq"
    }

    fun getDetail(q: String) {
        //To prevent re-render on change screen orientation
        if (userDetail.value?.login != null) return

        _isLoading.value = true
        val client = ApiConfig.getApiService().getUserDetail(q)
        client.enqueue(object : Callback<UserDetailResponse> {
            override fun onResponse(
                call: Call<UserDetailResponse>,
                response: Response<UserDetailResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _userDetail.value = responseBody
                    }
                } else {
                    _errorMsg.value = Event("Server Error, ${response.message()}")
                    Log.d(TAG, "onResponseFail: ${response.message()} ")
                }
            }

            override fun onFailure(call: Call<UserDetailResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMsg.value = Event("Error, cek koneksi anda!")
                Log.d(TAG, "onFailure: ${t.message}")
            }

        })
    }

    fun getFollow(type: String, username: String) {
        //To prevent re-render on change screen orientation
        if(followList.value != null) return

        _isLoading.value = true
        val client = if (type == "followers") {
            ApiConfig.getApiService().getFollowers(username)
        } else {
            ApiConfig.getApiService().getFollowing(username)
        }

        client.enqueue(object : Callback<List<FollowResponseItem>> {
            override fun onResponse(
                call: Call<List<FollowResponseItem>>,
                response: Response<List<FollowResponseItem>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _followList.value = responseBody
                    }
                } else {
                    _errorMsg.value = Event("Server Error, ${response.message()}")
                    Log.d(TAG, "onResponseFail: ${response.message()} ")
                }
            }

            override fun onFailure(call: Call<List<FollowResponseItem>>, t: Throwable) {
                _isLoading.value = false
                _errorMsg.value = Event("Error, cek koneksi anda!")
                Log.d(TAG, "onFailure: ${t.message}")
            }

        })
    }
}
