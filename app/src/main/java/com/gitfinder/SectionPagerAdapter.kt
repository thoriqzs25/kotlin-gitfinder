package com.gitfinder

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SectionPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2
    }

    var username: String = "thoriqzs25"
    override fun createFragment(position: Int): Fragment {
        return FollowFragment.newInstance(position + 1, username)
    }
}