package com.gitfinder.adapter.rv

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gitfinder.R
import com.gitfinder.UserItem
import com.gitfinder.database.FavoriteUser
import com.gitfinder.databinding.UserCardBinding


class UsersAdapter(
    private val listUser: List<UserItem>,
    private val onClickCard: (UserItem) -> Unit,
    private val onClickFav: (UserItem) -> Unit
) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private val TAG: String? = "usersadapterthoriq"
    private var favUsers: List<FavoriteUser> = emptyList()

    inner class ViewHolder(var binding: UserCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserItem) {
            itemView.setOnClickListener {
                onClickCard(user)
            }

            binding.ivFavorite.setOnClickListener {
                onClickFav(user)
            }

            binding.userName.text = user.login

            if (favUsers.isNotEmpty()) {
                val isFavoriteUser = favUsers.any { it.username == user.login }
                if (isFavoriteUser) {
                    binding.ivFavorite.setImageResource(R.drawable.ic_favorite_fill)
                } else {
                    binding.ivFavorite.setImageResource(R.drawable.ic_favorite_outline)
                }
            }

            Glide.with(binding.root)
                .load(user.avatarUrl)
                .placeholder(R.drawable.account_circle)
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

    fun updateFavoriteUsers(users: List<FavoriteUser>) {
        favUsers = users
        notifyDataSetChanged()
    }
}