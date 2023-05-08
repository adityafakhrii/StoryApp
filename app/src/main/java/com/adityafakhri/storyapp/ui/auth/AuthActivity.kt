package com.adityafakhri.storyapp.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adityafakhri.storyapp.R
import com.adityafakhri.storyapp.databinding.ActivityAuthBinding
import com.adityafakhri.storyapp.ui.login.LoginFragment

class AuthActivity : AppCompatActivity() {

    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, LoginFragment.newInstance())
                .commit()
        }
    }
}