package com.gitfinder.ui

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.gitfinder.R
import com.gitfinder.UserDetailResponse
import com.gitfinder.adapter.SectionPagerAdapter
import com.gitfinder.adapter.viewmodel.DetailViewModel
import com.gitfinder.database.FavoriteUser
import com.gitfinder.databinding.ActivityDetailBinding
import com.gitfinder.helper.DateConverter
import com.gitfinder.helper.Event
import com.gitfinder.helper.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : AppCompatActivity() {

    private lateinit var detailViewModel: DetailViewModel

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        _binding = ActivityDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val username = intent.getStringExtra(resources.getString(R.string.stringExtra))

        detailViewModel = obtainViewModel(this@DetailActivity)

        detailViewModel.userDetail.observe(this) {
            setUserDetail(it)
            setOnClickFab(it)
            binding.tvErrorDisplay.visibility = View.GONE

            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                binding.profileCard.updatePadding(top = 0, bottom = 4)
            }

            val sectionPagerAdapter = SectionPagerAdapter(this, it.login!!)
            binding.viewPager.adapter = sectionPagerAdapter

            TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
                val currTab = resources.getString(TAB_TITLES[position])
                val count = if (currTab == resources.getString(R.string.followersTab)) {
                    detailViewModel.userDetail.value?.followers
                } else {
                    detailViewModel.userDetail.value?.following
                }

                tab.text = "$count $currTab"
            }.attach()
            supportActionBar?.elevation = 0f

            if (detailViewModel.isFavorite(it.login)) {
                binding.fabFav?.setImageResource(R.drawable.ic_favorite_fill)
            } else {
                binding.fabFav?.setImageResource(R.drawable.ic_favorite_outline)
            }
        }

        detailViewModel.isLoading.observe(this) { loading ->
            showLoading(loading)
        }

        detailViewModel.errorMsg.observe(this) { msg ->
            setErrorMessage(msg)
        }

        detailViewModel.getDetail(username!!)

        binding.backTab.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        detailViewModel.userDetail.observe(this) {
            if (detailViewModel.isFavorite(it.login!!)) {
                binding.fabFav?.setImageResource(R.drawable.ic_favorite_fill)
            } else {
                binding.fabFav?.setImageResource(R.drawable.ic_favorite_outline)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setUserDetail(detail: UserDetailResponse) {
        val dateStr = detail.createdAt
        val convertedDate = DateConverter().formatDate(dateStr!!)

        binding.tvUsername.text = detail.login
        binding.tvName!!.text = if (detail.name.isNullOrEmpty()) "Undefined No Name" else detail.name
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

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: DetailActivity): DetailViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[DetailViewModel::class.java]
    }

    private fun setErrorMessage(msg: Event<String>) {
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

    private fun setOnClickFab(user: UserDetailResponse) {
        binding.fabFav?.setOnClickListener {
            var favoriteUser = FavoriteUser()
            favoriteUser.username = user.login!!
            favoriteUser.avatarUrl = user.avatarUrl
            favoriteUser.htmlUtl = user.htmlUrl!!

            if (detailViewModel.isFavorite(favoriteUser.username)) {
                detailViewModel.removeFavorite(favoriteUser)
                binding.fabFav?.setImageResource(R.drawable.ic_favorite_outline)

            } else {
                detailViewModel.addFavorite(favoriteUser)
                binding.fabFav?.setImageResource(R.drawable.ic_favorite_fill)
            }
        }
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }
}