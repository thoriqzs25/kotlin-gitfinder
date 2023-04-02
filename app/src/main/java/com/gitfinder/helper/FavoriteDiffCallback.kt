package com.gitfinder.helper

import androidx.recyclerview.widget.DiffUtil
import com.gitfinder.database.FavoriteUser

class FavoriteDiffCallback(private val mOldFav: List<FavoriteUser>, private val mNewFav: List<FavoriteUser>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return mOldFav.size
    }

    override fun getNewListSize(): Int {
        return mNewFav.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldFav[oldItemPosition].username == mNewFav[newItemPosition].username
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldEmployee = mOldFav[oldItemPosition]
        val newEmployee = mNewFav[newItemPosition]
        return  oldEmployee.username == newEmployee.username && oldEmployee.avatarUrl == newEmployee.avatarUrl && oldEmployee.htmlUtl == newEmployee.htmlUtl
    }

}