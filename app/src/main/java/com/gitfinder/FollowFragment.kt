package com.gitfinder

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
import com.gitfinder.databinding.FragmentFollowBinding
import com.gitfinder.ui.DetailActivity

private const val ARG_POSITION = "param1"
private const val ARG_USERNAME = "param2"

class FollowFragment : Fragment() {
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
            position = it.getInt(ARG_POSITION)
            username = it.getString(ARG_USERNAME)
        }

        val detailViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[DetailViewModel::class.java]

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

    companion object {
        @JvmStatic
        private val TAG = "followfragmentthoriq"
        fun newInstance(param1: Int, param2: String) =
            FollowFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_POSITION, param1)
                    putString(ARG_USERNAME, param2)
                }
            }
    }
}