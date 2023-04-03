package com.gitfinder.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gitfinder.R
import com.gitfinder.adapter.rv.UsersAdapter
import com.gitfinder.adapter.viewmodel.FavoriteViewModel
import com.gitfinder.database.FavoriteUser
import com.gitfinder.databinding.ActivityFavoriteBinding
import com.gitfinder.helper.ViewModelFactory

class FavoriteActivity : AppCompatActivity() {

    private lateinit var favoriteViewModel: FavoriteViewModel

    private var _binding: ActivityFavoriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        _binding = ActivityFavoriteBinding.inflate(layoutInflater)

        setContentView(binding.root)

        favoriteViewModel = obtainViewModel(this@FavoriteActivity)

        favoriteViewModel.favoriteUsers.observe(this) {
            setFavUserList(it)
            favoriteViewModel.favoriteUsers.removeObservers(this)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvFavorite.layoutManager = layoutManager

        binding.backTab.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        favoriteViewModel.favoriteUsers.observe(this) {
            setFavUserList(it)
            favoriteViewModel.favoriteUsers.removeObservers(this)
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

    private fun setFavUserList(users: List<FavoriteUser>) {
        if (users.isNotEmpty()) {
            val adapter = UsersAdapter(users,
                onClickCard = {
                    val intent = Intent(this, DetailActivity::class.java)
                    intent.putExtra(resources.getString(R.string.stringExtra), it.username)
                    startActivity(intent)
                },
                onClickFav = {
                    var favoriteUser = FavoriteUser()
                    favoriteUser.username = it.username!!
                    favoriteUser.avatarUrl = it.avatarUrl
                    favoriteUser.htmlUtl = it.htmlUtl!!

                    val isAlreadyFav = favoriteViewModel.isFavorite(favoriteUser)
                    if (isAlreadyFav) {
                        favoriteViewModel.removeFavorite(favoriteUser)
                    } else {
                        favoriteViewModel.addFavorite(favoriteUser)
                    }
                })
            binding.rvFavorite.adapter = adapter
            binding.rvFavorite.visibility = View.VISIBLE

            favoriteViewModel.getFavoriteList().observe(this) { favoriteUsers ->
                adapter.updateFavoriteUsers(favoriteUsers)
            }


        } else {
            binding.rvFavorite.visibility = View.GONE
        }
    }
}
