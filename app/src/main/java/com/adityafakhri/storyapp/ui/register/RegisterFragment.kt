package com.adityafakhri.storyapp.ui.register


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.adityafakhri.storyapp.R
import com.adityafakhri.storyapp.data.viewmodel.RegisterViewModel
import com.adityafakhri.storyapp.data.viewmodel.ViewModelGeneralFactory
import com.adityafakhri.storyapp.databinding.FragmentRegisterBinding
import com.adityafakhri.storyapp.ui.auth.AuthActivity
import com.adityafakhri.storyapp.ui.login.LoginFragment
import com.adityafakhri.storyapp.utils.Const

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var viewModel: RegisterViewModel? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playAnimation()

        viewModel = ViewModelProvider(
            this,
            ViewModelGeneralFactory((activity as AuthActivity))
        )[RegisterViewModel::class.java]

        viewModel?.let { vm ->
            vm.registerResult.observe(viewLifecycleOwner) { register ->
                if (!register.error) {
                    toLogin()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.register_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            vm.error.observe(viewLifecycleOwner) { error ->
                if (error.isNotEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.register_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            vm.loading.observe(viewLifecycleOwner) { state ->
                binding.viewLoading.visibility = state
            }
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                email.isEmpty() or password.isEmpty() or name.isEmpty() -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.field_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
                else -> {
                    viewModel?.register(name, email, password)
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            toLogin()
        }
    }

    private fun toLogin() {
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.container, LoginFragment(), LoginFragment::class.java.simpleName)
            commit()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -40f, 40f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title =
            ObjectAnimator.ofFloat(binding.titleRegisterTextView, View.ALPHA, 1f).setDuration(500)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.etName, View.ALPHA, 1f).setDuration(500)
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
                nameEditTextLayout,
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
}