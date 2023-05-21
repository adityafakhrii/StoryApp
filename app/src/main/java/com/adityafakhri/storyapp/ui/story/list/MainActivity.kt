package com.adityafakhri.storyapp.ui.story.list

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adityafakhri.storyapp.R
import com.adityafakhri.storyapp.data.source.local.AuthPreferences
import com.adityafakhri.storyapp.data.source.local.dataStore
import com.adityafakhri.storyapp.data.source.remote.retrofit.ApiConfig
import com.adityafakhri.storyapp.data.viewmodel.*
import com.adityafakhri.storyapp.databinding.ActivityMainBinding
import com.adityafakhri.storyapp.ui.adapter.ListStoryAdapter
import com.adityafakhri.storyapp.ui.adapter.LoadingStateAdapter
import com.adityafakhri.storyapp.ui.auth.AuthActivity
import com.adityafakhri.storyapp.ui.story.add.AddStoryActivity
import com.adityafakhri.storyapp.ui.story.maps.StoryMapsActivity
import com.adityafakhri.storyapp.utils.Const

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val listStoryAdapter = ListStoryAdapter()
    private lateinit var viewModel: MainViewModel

    private var tokenKey = ""


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = AuthPreferences.getInstance(dataStore)
        val authViewModel =
            ViewModelProvider(this, ViewModelAuthFactory(pref))[AuthViewModel::class.java]

        authViewModel.getUserPreferences(Const.UserPreferences.Token.name).observe(this) { token ->
            if (token != "Not Set") {
                tokenKey = "Bearer $token"

                val factory = ViewModelStoryFactory(this, ApiConfig.getApiService(), tokenKey)
                viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

                viewModel.storyList.observe(this@MainActivity) {
                    listStoryAdapter.apply {
                        submitData(lifecycle, it)
                        notifyDataSetChanged()
                    }
                }
            }
        }
        setRv()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        val pref = AuthPreferences.getInstance(dataStore)
        val authViewModel =
            ViewModelProvider(this, ViewModelAuthFactory(pref))[AuthViewModel::class.java]

        when (menuItem.itemId) {
            R.id.menu_add -> {
                Intent(this, AddStoryActivity::class.java).also {
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                }
            }
            R.id.menu_maps -> {
                Intent(this, StoryMapsActivity::class.java).also {
                    startActivity(it)
                }
            }
            R.id.menu_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.menu_logout -> {
                authViewModel.clearUserPreferences()
                Toast.makeText(
                    applicationContext,
                    getString(R.string.logout_sucess),
                    Toast.LENGTH_SHORT
                ).show()
                Intent(this, AuthActivity::class.java).also { intent ->
                    startActivity(intent)
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setRv() {
        binding.rvStory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter =
                listStoryAdapter.withLoadStateFooter(footer = LoadingStateAdapter { listStoryAdapter.retry() })
            smoothScrollToPosition(0)
        }
        listStoryAdapter.notifyDataSetChanged()

        listStoryAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.rvStory.scrollToPosition(0)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        listStoryAdapter.refresh()
        binding.rvStory.smoothScrollToPosition(0)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finishAffinity()
        return true
    }
}