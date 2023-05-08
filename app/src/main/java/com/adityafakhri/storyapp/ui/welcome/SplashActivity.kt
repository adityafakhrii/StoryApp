package com.adityafakhri.storyapp.ui.welcome

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.adityafakhri.storyapp.utils.Const
import com.adityafakhri.storyapp.data.source.local.AuthPreferences
import com.adityafakhri.storyapp.data.source.local.dataStore
import com.adityafakhri.storyapp.databinding.ActivitySplashBinding
import com.adityafakhri.storyapp.ui.auth.AuthActivity
import com.adityafakhri.storyapp.ui.auth.AuthViewModel
import com.adityafakhri.storyapp.data.viewmodel.ViewModelAuthFactory
import com.adityafakhri.storyapp.ui.story.list.MainActivity
import java.util.*
import kotlin.concurrent.schedule

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) window.insetsController?.hide(WindowInsets.Type.statusBars())
        else window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val pref = AuthPreferences.getInstance(dataStore)
        val authViewModel = ViewModelProvider(this, ViewModelAuthFactory(pref))[AuthViewModel::class.java]

        authViewModel.getUserPreferences(Const.UserPreferences.Token.name).observe(this@SplashActivity) { token ->
                if (token == Const.preferenceDefaultValue) Timer().schedule(2000) {
                    startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
                    finish()
                } else Timer().schedule(2000) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }
    }
}