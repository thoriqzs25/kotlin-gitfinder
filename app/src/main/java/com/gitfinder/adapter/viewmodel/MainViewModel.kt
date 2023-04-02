package com.gitfinder.adapter.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gitfinder.api.ApiConfig
import com.gitfinder.helper.Event
import com.gitfinder.SearchResponse
import com.gitfinder.database.FavoriteUser
import com.gitfinder.repository.FavoriteRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(application: Application): ViewModel() {
    private val mFavoriteRepository: FavoriteRepository = FavoriteRepository(application)

    private val _users = MutableLiveData<SearchResponse>()
    val users :LiveData<SearchResponse> = _users

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMsg = MutableLiveData<Event<String>>()
    val errorMsg: LiveData<Event<String>> = _errorMsg

    private val _favoriteUsers = MutableLiveData<List<FavoriteUser>>()
    val favoriteUsers :LiveData<List<FavoriteUser>> = _favoriteUsers

    init {

        mFavoriteRepository.getFavoriteList().observeForever { favoriteList ->
            _favoriteUsers.value = favoriteList
            Log.d(TAG, "line 33: $favoriteList")
        }
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
                        _users.value = responseBody!!
                    }
                }
                else {
                    _errorMsg.value = Event("Server Error, ${response.message()}")
                    Log.d(TAG, "onResponseFail: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMsg.value = Event("Error, cek koneksi anda!")
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun isFavorite(favoriteUser: FavoriteUser): Boolean {
        Log.d(TAG, "isFavorite: ${favoriteUsers.value}")
        return favoriteUsers.value?.any { it.username == favoriteUser.username } ?: false
    }

    fun getFavoriteList(): LiveData<List<FavoriteUser>> = mFavoriteRepository.getFavoriteList()

    fun addFavorite(favoriteUser: FavoriteUser) {
        mFavoriteRepository.addFavorite(favoriteUser)
    }

    fun removeFavorite(favoriteUser: FavoriteUser) {
        mFavoriteRepository.removeFavorite(favoriteUser)
    }

    companion object {
        private const val TAG = "mainviewmodelthoriq"
    }
}