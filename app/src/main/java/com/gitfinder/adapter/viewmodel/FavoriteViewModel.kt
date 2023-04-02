package com.gitfinder.adapter.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gitfinder.database.FavoriteUser
import com.gitfinder.repository.FavoriteRepository

class FavoriteViewModel(application: Application) : ViewModel() {
    private val mFavoriteRepository: FavoriteRepository = FavoriteRepository(application)

    fun getFavoriteList(): LiveData<List<FavoriteUser>> = mFavoriteRepository.getFavoriteList()

    fun addFavorite(favoriteUser: FavoriteUser) {
        mFavoriteRepository.addFavorite(favoriteUser)
    }
    fun removeFavorite(favoriteUser: FavoriteUser) {
        mFavoriteRepository.removeFavorite(favoriteUser)
    }
}