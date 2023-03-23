package com.gitfinder

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.gitfinder.databinding.ActivityDetailBinding
import com.gitfinder.databinding.ActivityMainBinding

class DetailActivity : AppCompatActivity() {
    companion object {
        private val TAG = "detailactivitythoriq"
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        setContentView(binding.root)

        val username = intent.getStringExtra("q")

        val detailViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[DetailViewModel::class.java]

        detailViewModel.userDetail.observe(this) {
            setUserDetail(it)
        }

        detailViewModel.isLoading.observe(this) { loading ->
            showLoading(loading)
        }

        if (username != null) {
            detailViewModel.getDetail(username)
        }
    }

    private fun setUserDetail(detail: UserDetailResponse) {
        Log.d(TAG, "setUserDetail: ${detail.login}")
        binding.centertext.text = detail.login.toString()
        Glide.with(this)
            .load(detail.avatarUrl)
            .into(binding.centerimage)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}