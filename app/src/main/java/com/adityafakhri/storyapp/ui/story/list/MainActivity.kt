package com.adityafakhri.storyapp.ui.story.list

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.adityafakhri.storyapp.utils.Const
import com.adityafakhri.storyapp.R
import com.adityafakhri.storyapp.data.source.local.AuthPreferences
import com.adityafakhri.storyapp.data.source.local.dataStore
import com.adityafakhri.storyapp.data.viewmodel.ViewModelAuthFactory
import com.adityafakhri.storyapp.data.viewmodel.ViewModelGeneralFactory
import com.adityafakhri.storyapp.databinding.ActivityMainBinding
import com.adityafakhri.storyapp.ui.adapter.ListStoryAdapter
import com.adityafakhri.storyapp.ui.auth.AuthActivity
import com.adityafakhri.storyapp.data.viewmodel.AuthViewModel
import com.adityafakhri.storyapp.data.viewmodel.MainViewModel
import com.adityafakhri.storyapp.ui.story.add.AddStoryActivity

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var viewModel: MainViewModel? = null
    private var tokenKey = ""

    private val listStoryAdapter = ListStoryAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = AuthPreferences.getInstance(dataStore)
        val authViewModel = ViewModelProvider(this, ViewModelAuthFactory(pref))[AuthViewModel::class.java]

        viewModel = ViewModelProvider(this, ViewModelGeneralFactory(this))[MainViewModel::class.java]

        authViewModel.getUserPreferences(Const.UserPreferences.Token.name).observe(this) { token ->
            if (token != "Not Set") {
                tokenKey = StringBuilder("Bearer ").append(token).toString()
                viewModel?.getStoryList(tokenKey)
            }
        }

        setRv()
        getAllStories()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        val pref = AuthPreferences.getInstance(dataStore)
        val authViewModel = ViewModelProvider(this, ViewModelAuthFactory(pref))[AuthViewModel::class.java]

        when (menuItem.itemId) {
            R.id.menu_add -> {
                Intent(this, AddStoryActivity::class.java).also {
                    startActivity(it)
                }
            }
            R.id.menu_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.menu_logout -> {
                authViewModel.clearUserPreferences()
                Toast.makeText(applicationContext, getString(R.string.logout_sucess), Toast.LENGTH_SHORT).show()
                Intent(this, AuthActivity::class.java).also { intent ->
                    startActivity(intent)
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun getAllStories() {
        viewModel?.apply {
            loading.observe(this@MainActivity) {
                binding.progressBar.visibility = it
            }

            error.observe(this@MainActivity) {
                if (it.isNotEmpty()) Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show() }

            storyList.observe(this@MainActivity) {
                listStoryAdapter.apply{
                    initData(it)
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun setRv() {
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = listStoryAdapter
            setHasFixedSize(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finishAffinity()
        return true
    }
}