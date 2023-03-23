package com.gitfinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.gitfinder.databinding.FragmentFollowBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_POSITION = "param1"
private const val ARG_USERNAME = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FollowFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FollowFragment : Fragment() {
    private var position: Int? = 0
    private var username: String? = null

    private lateinit var binding: FragmentFollowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowBinding.inflate(inflater, container, false)
        arguments?.let {
            position = it.getInt(ARG_POSITION)
            username = it.getString(ARG_USERNAME)
        }

        val detailViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[DetailViewModel::class.java]

        detailViewModel.followList.observe(viewLifecycleOwner) { list ->
            setFollowListData(list as List<FollowResponseItem>)
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
        val adapter = FollowAdapter(list, onClick = {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("q", it.login)
            startActivity(intent)
        })
        binding.rvFollow.adapter = adapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FollowersFragment.
         */
        // TODO: Rename and change types and number of parameters
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