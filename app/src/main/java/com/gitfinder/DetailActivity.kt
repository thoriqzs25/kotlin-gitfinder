package com.gitfinder

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.gitfinder.databinding.ActivityDetailBinding
import com.gitfinder.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : AppCompatActivity() {
    companion object {
        private val TAG = "detailactivitythoriq"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
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
            if (username != it.login) {
                detailViewModel.getFollow("followers", it.login!!)
            }

            val sectionPagerAdapter = SectionPagerAdapter(this, it.login!!)
            binding.viewPager.adapter = sectionPagerAdapter

            TabLayoutMediator(binding.tabs, binding.viewPager) {tab, position ->
                val currTab = resources.getString(TAB_TITLES[position])
                val count = if (currTab == "followers") {
                    detailViewModel.userDetail.value?.followers
                } else {
                    detailViewModel.userDetail.value?.following
                }

                tab.text = "$count $currTab"
            }.attach()
            supportActionBar?.elevation = 0f
        }

        detailViewModel.isLoading.observe(this) { loading ->
            showLoading(loading)
        }

        if (username != null) {
            detailViewModel.getDetail(username)
        }
    }

    private fun setUserDetail(detail: UserDetailResponse) {
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