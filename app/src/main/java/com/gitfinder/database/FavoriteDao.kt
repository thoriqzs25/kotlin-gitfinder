package com.gitfinder.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addFavorite(favoriteUser: FavoriteUser)

    @Delete
    fun removeFavorite(favoriteUser: FavoriteUser)

    @Query("SELECT * from favorite_user")
    fun getFavoriteList(): LiveData<List<FavoriteUser>>
}