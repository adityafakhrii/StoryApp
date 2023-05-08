package com.adityafakhri.storyapp.ui.story.detail

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adityafakhri.storyapp.utils.Const
import com.adityafakhri.storyapp.R
import com.adityafakhri.storyapp.databinding.ActivityDetailStoryBinding
import com.bumptech.glide.Glide

class DetailStoryActivity : AppCompatActivity() {

    private var _binding: ActivityDetailStoryBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("AppCompatMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportPostponeEnterTransition()

        val img: String = intent.getData(Const.StoryDetail.ImageURL.name, "")
        val name: String = intent.getData(Const.StoryDetail.UserName.name, "Name")
        val description: String = intent.getData(Const.StoryDetail.ContentDescription.name, "Description")

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        val titleName = getString(R.string.name_detail, name)
        actionBar?.title = titleName

        Glide.with(binding.root)
            .load(img)
            .into(binding.ivStoryImage)
        binding.apply {
            tvName.text = name
            tvDescription.text = description
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return true
    }

    private fun Intent.getData(key: String, defaultValue: String = "None"): String {
        return getStringExtra(key) ?: defaultValue
    }

}