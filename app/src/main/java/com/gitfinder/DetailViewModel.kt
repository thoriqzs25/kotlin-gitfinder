package com.gitfinder

import android.util.Log
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

    private val _followersDetail = MutableLiveData<FollowersResponse>()
    val followersDetail: LiveData<FollowersResponse> = _followersDetail

    private val _followingDetail = MutableLiveData<FollowingResponse>()
    val followingDetail: LiveData<FollowingResponse> = _followingDetail

    companion object {
        private const val TAG = "detailviewmodel"
    }

    fun getDetail(q: String) {
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
                    Log.d(TAG, "onResponseFail: ${response.message()} ")
                }
            }

            override fun onFailure(call: Call<UserDetailResponse>, t: Throwable) {
                _isLoading.value = false
                Log.d(TAG, "onFailure: ${t.message}")
            }

        })
    }
}
