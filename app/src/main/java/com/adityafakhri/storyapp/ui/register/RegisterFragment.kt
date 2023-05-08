package com.adityafakhri.storyapp.ui.register


import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.adityafakhri.storyapp.utils.Const
import com.adityafakhri.storyapp.R
import com.adityafakhri.storyapp.data.viewmodel.ViewModelGeneralFactory
import com.adityafakhri.storyapp.databinding.FragmentRegisterBinding
import com.adityafakhri.storyapp.ui.auth.AuthActivity
import com.adityafakhri.storyapp.ui.login.LoginFragment

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var viewModel: RegisterViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, ViewModelGeneralFactory((activity as AuthActivity)))[RegisterViewModel::class.java]

        viewModel?.let { vm ->
            vm.registerResult.observe(viewLifecycleOwner) { register ->
                if (!register.error) {
                    toLogin()
                    Toast.makeText(requireContext(), getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                }
            }
            vm.error.observe(viewLifecycleOwner) { error ->
                if (error.isNotEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.register_failed), Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), getString(R.string.field_empty), Toast.LENGTH_SHORT).show()
                }
                !email.matches(Const.emailFormat) -> {
                    Toast.makeText(requireContext(), getString(R.string.email_invalid), Toast.LENGTH_SHORT).show()
                }
                password.length <= 8 -> {
                    Toast.makeText(requireContext(), getString(R.string.password_invalid), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    viewModel?.register(name, email, password)
                }
            }
        }

        toLogin()
    }

    private fun toLogin() {
        binding.btnLogin.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.container, LoginFragment(), LoginFragment::class.java.simpleName)
                commit()
            }
        }
    }
}