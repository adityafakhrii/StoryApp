package com.adityafakhri.storyapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.adityafakhri.storyapp.data.source.remote.response.ListStoryItem
import com.adityafakhri.storyapp.databinding.ItemStoryBinding
import com.adityafakhri.storyapp.ui.story.detail.DetailStoryActivity
import com.adityafakhri.storyapp.utils.Const
import com.bumptech.glide.Glide

class ListStoryAdapter : PagingDataAdapter<ListStoryItem, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ListViewHolder, position: Int) {
       getItem(position)?.let { viewHolder.bind(it) }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    inner class ListViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            with(binding) {
                tvStoryUsername.text = story.name
                Glide.with(itemView)
                    .load(story.photoUrl)
                    .into(ivStoryImage)

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                    intent.putExtra(Const.StoryDetail.UserName.name, story.name)
                    intent.putExtra(Const.StoryDetail.ImageURL.name, story.photoUrl)
                    intent.putExtra(Const.StoryDetail.ContentDescription.name, story.description)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                    itemView.context.startActivity(intent)

                }
            }
        }
    }
}
