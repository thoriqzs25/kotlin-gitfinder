package com.gitfinder.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.gitfinder.adapter.viewmodel.DetailViewModel
import com.gitfinder.adapter.viewmodel.FavoriteViewModel
import com.gitfinder.database.FavoriteUser
import com.gitfinder.databinding.ActivityFavoriteBinding
import com.gitfinder.helper.ViewModelFactory

class FavoriteActivity : AppCompatActivity() {

    private lateinit var favoriteViewModel: FavoriteViewModel

    private val TAG = "favoriteactivitythoriq"

    private var _binding: ActivityFavoriteBinding? = null
    private val binding get() = _binding!!

    private var favUsers: List<FavoriteUser>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        _binding = ActivityFavoriteBinding.inflate(layoutInflater)

        setContentView(binding.root)

        favoriteViewModel = obtainViewModel(this@FavoriteActivity)

        favoriteViewModel.favoriteUsers.observe(this) {
            Log.d(TAG, "onCreate: line 34, ${it.size}")
            favUsers = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun obtainViewModel(activity: FavoriteActivity): FavoriteViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[FavoriteViewModel::class.java]
    }

//    private fun setFavUserList(users: List<User>) {
//        if (users.isNotEmpty())
//    }
}
