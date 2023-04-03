package com.gitfinder.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gitfinder.R
import com.gitfinder.SearchResponse
import com.gitfinder.UserItem
import com.gitfinder.adapter.rv.UsersAdapter
import com.gitfinder.adapter.viewmodel.MainViewModel
import com.gitfinder.database.FavoriteUser
import com.gitfinder.databinding.ActivityMainBinding
import com.gitfinder.datastore.SettingPreferences
import com.gitfinder.helper.Event
import com.gitfinder.helper.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var appCompatDelegate: AppCompatDelegate
    private lateinit var mainViewModel: MainViewModel
    private lateinit var pref: SettingPreferences

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = SettingPreferences.getInstance(dataStore)

        appCompatDelegate = AppCompatDelegate.create(this, null)

        mainViewModel = obtainViewModel(this@MainActivity)

        mainViewModel.users.observe(this) { users ->
            setUsersListData(users as SearchResponse)
        }

        mainViewModel.isLoading.observe(this) { loading ->
            showLoading(loading)
        }

        mainViewModel.errorMsg.observe(this) {msg ->
            setErrorMessage(msg)
        }

        binding.ivFavList.setOnClickListener {
            val intent = Intent(this@MainActivity, FavoriteActivity::class.java)
            startActivity(intent)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvUserList.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvUserList.addItemDecoration(itemDecoration)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = binding.searchUser
        initializeSearchView(searchView, searchManager)

        binding.cvTheme.setOnClickListener {
            toggleTheme()
        }

        mainViewModel.getThemeSettings().observe(this) { isDarkMode ->
            if (isDarkMode ) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.ivTheme.setImageResource(R.drawable.ic_light_mode)
                binding.ivTheme.tag = getString(R.string.dark)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.ivTheme.setImageResource(R.drawable.ic_dark_mode)
                binding.ivTheme.tag = getString(R.string.light)
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        Log.d("mainactivitythoriq", "onResume: ")
//////        mainViewModel.getThemeSettings().observe(this) { isDarkMode ->
//////            if (isDarkMode) {
//////                binding.ivTheme.setImageResource(R.drawable.ic_light_mode)
//////                binding.ivTheme.tag = getString(R.string.dark)
//////            } else {
//////                binding.ivTheme.setImageResource(R.drawable.ic_dark_mode)
//////                binding.ivTheme.tag = getString(R.string.light)
//////            }
//////        }
//    }

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
        val factory = ViewModelFactory.getInstance(activity.application, pref)
        return ViewModelProvider(activity, factory)[MainViewModel::class.java]
    }

    private fun setErrorMessage(msg: Event<String>) {
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

    private fun initializeSearchView(searchView: SearchView, searchManager: SearchManager) {
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

    private fun toggleTheme() {
        val isDarkTheme: Boolean = when (binding.ivTheme.tag) {
            resources.getString(R.string.dark) -> true
            resources.getString(R.string.light) -> false
            else -> throw IllegalArgumentException("Unknown theme tag: ${binding.ivTheme.tag}")
        }
        mainViewModel.saveThemeSetting(!isDarkTheme)
    }
}