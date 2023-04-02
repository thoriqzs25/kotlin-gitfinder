package com.gitfinder.adapter.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gitfinder.database.FavoriteUser
import com.gitfinder.repository.FavoriteRepository

class FavoriteViewModel(application: Application) : ViewModel() {
    private val mFavoriteRepository: FavoriteRepository = FavoriteRepository(application)

    private val _favoriteUsers = MutableLiveData<List<FavoriteUser>>()
    val favoriteUsers :LiveData<List<FavoriteUser>> = _favoriteUsers

    init {
        mFavoriteRepository.getFavoriteList().observeForever { favoriteList ->
            _favoriteUsers.value = favoriteList
        }
    }

    fun isFavorite(favoriteUser: FavoriteUser): Boolean {
        return favoriteUsers.value?.any { it.username == favoriteUser.username } ?: false
    }
    fun getFavoriteList(): LiveData<List<FavoriteUser>> = mFavoriteRepository.getFavoriteList()

    fun addFavorite(favoriteUser: FavoriteUser) {
        mFavoriteRepository.addFavorite(favoriteUser)
    }
    fun removeFavorite(favoriteUser: FavoriteUser) {
        mFavoriteRepository.removeFavorite(favoriteUser)
    }
}