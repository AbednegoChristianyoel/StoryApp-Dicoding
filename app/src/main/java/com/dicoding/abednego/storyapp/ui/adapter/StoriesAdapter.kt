package com.dicoding.abednego.storyapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dicoding.abednego.storyapp.data.api.response.ListStoryItem
import com.dicoding.abednego.storyapp.databinding.ItemRowStoryBinding
import com.dicoding.abednego.storyapp.ui.detail.DetailStoryActivity

class StoriesAdapter :
    PagingDataAdapter<ListStoryItem, StoriesAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
            holder.itemView.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, DetailStoryActivity::class.java).apply {
                    putExtra(EXTRA_NAME, data.name)
                    putExtra(EXTRA_DESCRIPTION, data.description)
                    putExtra(EXTRA_PHOTO, data.photoUrl)
                }
                context.startActivity(intent)
            }
        }
    }

    class MyViewHolder(private val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            binding.tvUsername.text = data.name
            binding.tvDescription.text = data.description
            Glide.with(binding.ivContent)
                .load(data.photoUrl)
                .transition(DrawableTransitionOptions.withCrossFade()) // Fade transition
                .into(binding.ivContent)
        }
    }

    companion object {
        const val EXTRA_NAME = "EXTRA_NAME"
        const val EXTRA_PHOTO = "EXTRA_PHOTO"
        const val EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION"

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}