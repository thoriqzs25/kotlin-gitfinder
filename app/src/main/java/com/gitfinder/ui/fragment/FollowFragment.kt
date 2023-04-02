package com.gitfinder.ui.fragment

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.gitfinder.adapter.viewmodel.DetailViewModel
import com.gitfinder.adapter.rv.FollowAdapter
import com.gitfinder.FollowResponseItem
import com.gitfinder.databinding.FragmentFollowBinding
import com.gitfinder.helper.ViewModelFactory
import com.gitfinder.ui.DetailActivity

class FollowFragment : Fragment() {
    private lateinit var detailViewModel: DetailViewModel

    private var position: Int? = 0
    private var username: String? = null

    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFollowBinding.inflate(inflater, container, false)

        arguments?.let {
            position = it.getInt("position")
            username = it.getString("username")
        }

        detailViewModel = obtainViewModel(this)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.tvTotalFollow.visibility = View.GONE
            binding.rvFollow.updatePadding(top = 0)
        }

        detailViewModel.followList.observe(viewLifecycleOwner) { list ->
            setFollowListData(list as List<FollowResponseItem>)
        }

        detailViewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            showLoading(loading)
        }

        val layoutManager = LinearLayoutManager(this.context)
        binding.rvFollow.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this.context, layoutManager.orientation)
        binding.rvFollow.addItemDecoration(itemDecoration)

        if (position == 1) {
            detailViewModel.getFollow("followers", username!!)
        } else {
            detailViewModel.getFollow("following", username!!)
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setFollowListData(list: List<FollowResponseItem>) {
        binding.tvTotalFollow.text = "Showing ${list.size} results"
        if (list.isNotEmpty()) {
            val adapter = FollowAdapter(list, onClick = {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("q", it.login)
                startActivity(intent)
            })
            binding.rvFollow.adapter = adapter
        } else {
            binding.emptyList.text = "List kosong"
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun obtainViewModel(fragment: Fragment): DetailViewModel {
        val factory = ViewModelFactory.getInstance(fragment.requireActivity().application)
        return ViewModelProvider(fragment, factory)[DetailViewModel::class.java]
    }

    companion object {
        private val TAG = "followfragmentthoriq"
    }
}