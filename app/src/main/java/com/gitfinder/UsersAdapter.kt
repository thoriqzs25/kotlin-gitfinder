package com.gitfinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gitfinder.databinding.UserCardBinding


class UsersAdapter(private val listUser: List<UserItem>, private val onClick: (UserItem) -> Unit) :
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {
    inner class ViewHolder(var binding: UserCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserItem) {
            itemView.setOnClickListener {
                onClick(user)
            }
            binding.userName.text = user.login
            Glide.with(binding.root)
                .load(user.avatarUrl)
                .into(binding.userImage)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            UserCardBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listUser[position])
    }

    override fun getItemCount() = listUser.size


}