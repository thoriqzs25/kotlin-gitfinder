package com.gitfinder.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.gitfinder.database.FavoriteDao
import com.gitfinder.database.FavoriteUser
import com.gitfinder.database.MainDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainRepository(application: Application) {
    private val mFavoriteDao: FavoriteDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = MainDatabase.getDatabase(application)
        mFavoriteDao = db.favUserDao()
    }

    fun getFavoriteList(): LiveData<List<FavoriteUser>> = mFavoriteDao.getFavoriteList()

    fun addFavorite(favoriteUser: FavoriteUser) {
        executorService.execute {
            mFavoriteDao.addFavorite(favoriteUser)
        }
    }

    fun removeFavorite(favoriteUser: FavoriteUser) {
        executorService.execute {
            mFavoriteDao.removeFavorite(favoriteUser)
        }
    }
}