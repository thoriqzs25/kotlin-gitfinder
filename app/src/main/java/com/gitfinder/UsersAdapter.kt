package com.gitfinder

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gitfinder.databinding.UserCardBinding


class UsersAdapter(private val listUser: List<UserItem>) :
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount() = listUser.size


    class ViewHolder(var binding: UserCardBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(user: UserItem) {
//            binding.userName.text =
//        }
    }
}