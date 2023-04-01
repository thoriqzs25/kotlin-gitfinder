package com.gitfinder

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gitfinder.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[MainViewModel::class.java]

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

    private fun setUsersListData(users: SearchResponse) {

        val adapter = UsersAdapter(users.items as List<UserItem>, onClick = {
            val intent = Intent(this@MainActivity, DetailActivity::class.java)
            intent.putExtra("q", it.login)
            startActivity(intent)
        })
        binding.rvUserList.adapter = adapter
        binding.tvTotalres.text = resources.getString(R.string.resultText, users.totalCount, users.items.size)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        private val TAG = "mainactivitythoriq"
    }
}