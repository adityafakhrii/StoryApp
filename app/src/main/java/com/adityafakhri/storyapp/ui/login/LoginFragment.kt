package com.adityafakhri.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.adityafakhri.storyapp.R
import com.adityafakhri.storyapp.data.source.local.AuthPreferences
import com.adityafakhri.storyapp.data.source.local.dataStore
import com.adityafakhri.storyapp.data.viewmodel.AuthViewModel
import com.adityafakhri.storyapp.data.viewmodel.LoginViewModel
import com.adityafakhri.storyapp.data.viewmodel.ViewModelAuthFactory
import com.adityafakhri.storyapp.data.viewmodel.ViewModelGeneralFactory
import com.adityafakhri.storyapp.databinding.FragmentLoginBinding
import com.adityafakhri.storyapp.ui.auth.AuthActivity
import com.adityafakhri.storyapp.ui.register.RegisterFragment
import com.adityafakhri.storyapp.ui.story.list.MainActivity
import com.adityafakhri.storyapp.utils.Const


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var viewModel: LoginViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playAnimation()

        val pref = AuthPreferences.getInstance((activity as AuthActivity).dataStore)

        val authViewModel =
            ViewModelProvider(this, ViewModelAuthFactory(pref))[AuthViewModel::class.java]

        viewModel = ViewModelProvider(
            this,
            ViewModelGeneralFactory((activity as AuthActivity))
        )[LoginViewModel::class.java]

        viewModel?.let { vModel ->
            vModel.loginResult.observe(viewLifecycleOwner) {
                authViewModel.setUserPreferences(
                    it.loginResult.token
                )
            }
            vModel.error.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    Toast.makeText(activity, getString(R.string.login_failed), Toast.LENGTH_SHORT)
                        .show()
                }
            }
            vModel.loading.observe(viewLifecycleOwner) {
                binding.viewLoading.visibility = it
            }
        }

        authViewModel.getUserPreferences(Const.UserPreferences.Token.name)
            .observe(viewLifecycleOwner) { token ->
                val intent = Intent(activity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

                if (token != Const.preferenceDefaultValue)
                    (activity as AuthActivity)
                        .startActivity(intent)
            }

        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                !email.matches(Const.emailFormat) -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.email_invalid),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                password.length < 8 -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.password_invalid),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                email.isEmpty() or password.isEmpty() -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.field_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    viewModel?.checkLogin(email, password)
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.container, RegisterFragment(), RegisterFragment::class.java.simpleName)
                commit()
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -40f, 40f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title =
            ObjectAnimator.ofFloat(binding.titleLoginTextView, View.ALPHA, 1f).setDuration(500)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.etPassword, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailEditTextLayout,
                passwordEditTextLayout,
                login,
                register
            )
            startDelay = 500
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = LoginFragment()
    }
}
