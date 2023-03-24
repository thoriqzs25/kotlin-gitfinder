package com.gitfinder

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.gitfinder.databinding.ActivityDetailBinding
import com.gitfinder.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
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
            binding.tvErrorDisplay.visibility = View.GONE

            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                binding.profileCard.updatePadding(top = 0, bottom = 4)
            }

            val sectionPagerAdapter = SectionPagerAdapter(this, it.login!!)
            binding.viewPager.adapter = sectionPagerAdapter

            TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
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

        detailViewModel.errorMsg.observe(this) {msg ->
            msg.getContentIfNotHandled()?.let {
                binding.tvErrorDisplay.visibility = View.VISIBLE
                binding.tvErrorDisplay.text = it
                val snackbar = Snackbar.make(
                    window.decorView.rootView,
                    it,
                    Snackbar.LENGTH_SHORT
                )
                snackbar.anchorView = binding.botView
                snackbar.show()
            }
        }

        if (username != null) {
            detailViewModel.getDetail(username)
        }

        binding.backTab.setOnClickListener {
            finish()
        }
    }

    private fun setUserDetail(detail: UserDetailResponse) {
        val dateStr = detail.createdAt
        val convertedDate = DateConverter().formatDate(dateStr!!)

        binding.tvUsername.text = detail.login
        binding.tvUsersince.text = "Member since $convertedDate"
        binding.ivUserimage.borderColor = resources.getColor(R.color.white)
        binding.ivUserimage.borderWidth = 2
        Glide.with(this)
            .load(detail.avatarUrl)
            .placeholder(R.drawable.account_circle)
            .into(binding.ivUserimage)

        binding.icGithub.setOnClickListener {
            val webIntent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(detail.htmlUrl))
            startActivity(webIntent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}