package com.gitfinder.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gitfinder.*
import com.gitfinder.adapter.rv.UsersAdapter
import com.gitfinder.adapter.viewmodel.DetailViewModel
import com.gitfinder.adapter.viewmodel.FavoriteViewModel
import com.gitfinder.adapter.viewmodel.MainViewModel
import com.gitfinder.database.FavoriteUser
import com.gitfinder.databinding.ActivityMainBinding
import com.gitfinder.helper.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        mainViewModel = obtainViewModel(this@MainActivity)

        mainViewModel.users.observe(this) { users ->
            setUsersListData(users as SearchResponse)
        }

        mainViewModel.isLoading.observe(this) { loading ->
            showLoading(loading)
        }

        mainViewModel.errorMsg.observe(this) {msg ->
            msg.getContentIfNotHandled()?.let {
                val snackbar = Snackbar.make(
                    window.decorView.rootView,
                    it,
                    Snackbar.LENGTH_SHORT
                )
                snackbar.anchorView = binding.botView
                snackbar.show()
            }
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvUserList.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvUserList.addItemDecoration(itemDecoration)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = binding.searchUser as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_placeholder)
        searchView.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            searchView.clearFocus()
            imm.hideSoftInputFromWindow(searchView.windowToken, 0)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(q: String?): Boolean {
                if (q?.length!! >= 3) {
                    binding.rvUserList.visibility = RecyclerView.VISIBLE
                    mainViewModel.searchUsers(q.toString())
                } else {
                    binding.rvUserList.visibility = RecyclerView.GONE
                    binding.tvTotalres.text = resources.getString(R.string.search_me_text)
                }
                return false
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setUsersListData(users: SearchResponse) {
        val adapter = UsersAdapter(users.items as List<UserItem>,
            onClickCard = {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(resources.getString(R.string.stringExtra), it.login)
                startActivity(intent)
        },
            onClickFav = {
                var favoriteUser = FavoriteUser()
                favoriteUser.username = it.login!!
                favoriteUser.avatarUrl = it.avatarUrl
                favoriteUser.htmlUtl = it.htmlUrl!!

                if (mainViewModel.isFavorite(favoriteUser)) {
                    mainViewModel.removeFavorite(favoriteUser)
                } else {
                    mainViewModel.addFavorite(favoriteUser)
                }
            }
        )
        binding.rvUserList.adapter = adapter
        binding.tvTotalres.text = resources.getString(R.string.resultText, users.totalCount, users.items.size)

        mainViewModel.getFavoriteList().observe(this) { favoriteUsers ->
            adapter.updateFavoriteUsers(favoriteUsers)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun obtainViewModel(activity: MainActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[MainViewModel::class.java]
    }

    companion object {
        private val TAG = "mainactivitythoriq"
    }
}