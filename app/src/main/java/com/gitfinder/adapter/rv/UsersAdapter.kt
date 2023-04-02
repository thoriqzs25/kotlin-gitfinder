package com.gitfinder.adapter.rv

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gitfinder.FollowResponseItem
import com.gitfinder.R
import com.gitfinder.UserItem
import com.gitfinder.database.FavoriteUser
import com.gitfinder.databinding.UserCardBinding


class UsersAdapter<T>(
    private val listUser: List<T>?,
    private val onClickCard: (T) -> Unit,
    private val onClickFav: (T) -> Unit
) : RecyclerView.Adapter<UsersAdapter<T>.ViewHolder>() {

    private val TAG: String? = "usersadapterthoriq"
    private var favUsers: List<FavoriteUser> = emptyList()

    inner class ViewHolder(var binding: UserCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: T) {
            itemView.setOnClickListener {
                onClickCard(user)
            }

            binding.ivFavorite.setOnClickListener {
                onClickFav(user)
            }

            when (user) {
                is UserItem-> {
                    binding.userName.text = user.login
                }
                is FollowResponseItem -> {
                    binding.userName.text = user.login
                }
                is FavoriteUser -> {
                    binding.userName.text = user.username
                }
            }

            if (favUsers.isNotEmpty()) {
                val isFavoriteUser = favUsers.any {
                    when (user) {
                        is UserItem -> it.username == user.login
                        is FollowResponseItem -> it.username == user.login
                        is FavoriteUser -> it.username == user.username
                        else -> false
                    }
                }
                if (isFavoriteUser) {
                    binding.ivFavorite.setImageResource(R.drawable.ic_favorite_fill)
                } else {
                    binding.ivFavorite.setImageResource(R.drawable.ic_favorite_outline)
                }
            }


            Glide.with(binding.root)
                .load(when (user) {
                    is UserItem -> user.avatarUrl
                    is FollowResponseItem -> user.avatarUrl
                    is FavoriteUser -> user.avatarUrl
                    else -> null
                })
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
        holder.bind(listUser!![position])
    }

    override fun getItemCount() = listUser!!.size

    fun updateFavoriteUsers(users: List<FavoriteUser>) {
        favUsers = users
        notifyDataSetChanged()
    }
}