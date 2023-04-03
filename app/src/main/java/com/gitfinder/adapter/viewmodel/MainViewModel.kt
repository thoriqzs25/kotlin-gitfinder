package com.gitfinder.adapter.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.gitfinder.SearchResponse
import com.gitfinder.api.ApiConfig
import com.gitfinder.database.FavoriteUser
import com.gitfinder.datastore.SettingPreferences
import com.gitfinder.helper.Event
import com.gitfinder.repository.MainRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(application: Application, private val pref: SettingPreferences): ViewModel() {
    private val mMainRepository: MainRepository = MainRepository(application)

    private val _users = MutableLiveData<SearchResponse>()
    val users :LiveData<SearchResponse> = _users

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMsg = MutableLiveData<Event<String>>()
    val errorMsg: LiveData<Event<String>> = _errorMsg

    private val _favoriteUsers = MutableLiveData<List<FavoriteUser>>()
    val favoriteUsers :LiveData<List<FavoriteUser>> = _favoriteUsers

    init {
        mMainRepository.getFavoriteList().observeForever { favoriteList ->
            _favoriteUsers.value = favoriteList
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
        return favoriteUsers.value?.any { it.username == favoriteUser.username } ?: false
    }

    fun getFavoriteList(): LiveData<List<FavoriteUser>> = mMainRepository.getFavoriteList()

    fun addFavorite(favoriteUser: FavoriteUser) {
        mMainRepository.addFavorite(favoriteUser)
    }

    fun removeFavorite(favoriteUser: FavoriteUser) {
        mMainRepository.removeFavorite(favoriteUser)
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    companion object {
        private const val TAG = "mainviewmodelthoriq"
    }
}