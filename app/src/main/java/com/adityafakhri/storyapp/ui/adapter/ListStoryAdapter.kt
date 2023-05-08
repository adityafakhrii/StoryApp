package com.adityafakhri.storyapp.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adityafakhri.storyapp.data.source.remote.response.ListStoryItem
import com.adityafakhri.storyapp.databinding.ItemStoryBinding
import com.adityafakhri.storyapp.ui.story.detail.DetailStoryActivity
import com.adityafakhri.storyapp.utils.Const
import com.bumptech.glide.Glide

class ListStoryAdapter : RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>()  {

    private var listStory = mutableListOf<ListStoryItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int = listStory.size

    override fun onBindViewHolder(viewHolder: ListViewHolder, position: Int) {
        val story = listStory[position]
        viewHolder.bind(viewHolder.itemView.context, story)
    }

    fun initData(story: List<ListStoryItem>) {
        listStory.clear()
        listStory = story.toMutableList()
    }

    inner class ListViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, story: ListStoryItem) {
            with(binding) {
                tvStoryUsername.text = story.name
                Glide.with(itemView)
                    .load(story.photoUrl)
                    .into(ivStoryImage)

                itemView.setOnClickListener {
//                    onItemClickCallback.onItemClicked(story)

                    Intent(context, DetailStoryActivity::class.java).also { intent ->
                        intent.putExtra(Const.StoryDetail.UserName.name, story.name)
                        intent.putExtra(Const.StoryDetail.ImageURL.name, story.photoUrl)
                        intent.putExtra(Const.StoryDetail.ContentDescription.name, story.description)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}
