package com.gitfinder.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gitfinder.ui.fragment.FollowFragment

class SectionPagerAdapter(activity: AppCompatActivity, user: String) :
    FragmentStateAdapter(activity) {
    private val username: String = user
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = FollowFragment()
        val bundle = Bundle()
        bundle.putString("username", username)
        bundle.putInt("position", position + 1)
        fragment.arguments = bundle

        return fragment
    }
}