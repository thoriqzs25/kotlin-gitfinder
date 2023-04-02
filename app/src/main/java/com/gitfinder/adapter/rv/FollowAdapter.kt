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

class FollowAdapter(
    private val listFollow: List<FollowResponseItem>,
    private val onClickCard: (FollowResponseItem) -> Unit,
    private val onClickFav: (FollowResponseItem) -> Unit
) : RecyclerView.Adapter<FollowAdapter.ViewHolder>() {

    private var favUsers: List<FavoriteUser> = emptyList()

    inner class ViewHolder(var binding: UserCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: FollowResponseItem) {
            itemView.setOnClickListener {
                onClickCard(user)
            }

            binding.ivFavorite.setOnClickListener {
                onClickFav(user)
            }

            if (favUsers.isNotEmpty()) {
                val isFavoriteUser = favUsers.any { it.username == user.login }
                val TAG = "followadapterthoriq"
                Log.d(TAG, "bind: $isFavoriteUser")
                if (isFavoriteUser) {
                    binding.ivFavorite.setImageResource(R.drawable.ic_favorite_fill)
                } else {
                    binding.ivFavorite.setImageResource(R.drawable.ic_favorite_outline)
                }
            }

            binding.userName.text = user.login

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
        holder.bind(listFollow[position])
    }

    override fun getItemCount() = listFollow.size

    fun updateFavoriteUsers(users: List<FavoriteUser>) {
        favUsers = users
        notifyDataSetChanged()
    }
}