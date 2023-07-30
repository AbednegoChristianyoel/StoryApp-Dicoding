package com.dicoding.abednego.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.dicoding.abednego.storyapp.R
import com.dicoding.abednego.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.abednego.storyapp.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel = RegisterViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setLoginButtonEnable()

        binding.etFullname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setLoginButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {}
        })

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setLoginButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {}
        })

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setLoginButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {}
        })

        binding.btnRegister.setOnClickListener{
            val name = binding.etFullname.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.userRegister(name, email, password)
        }
        binding.tvHaveAccount.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message != null) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.toast_register_failed),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.registerResponse.observe(this) { registerResponse ->
            if (registerResponse != null) {
                Toast.makeText(this, getString(R.string.toast_register_success), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        playAnimation()
    }
    private fun setLoginButtonEnable() {
        val fullNameState = binding.etFullname.text.toString().isNotEmpty()
        val emailState = binding.etEmail.text.toString().isNotEmpty()
        val passwordState = binding.etPassword.text.toString().isNotEmpty() && binding.etPassword.error.isNullOrEmpty()
        binding.btnRegister.isEnabled = fullNameState && emailState && passwordState
    }

    private fun playAnimation() {

        val title = ObjectAnimator.ofFloat(binding.tvRegisterTitle, View.ALPHA, 1f).setDuration(500)
        val usernameTV = ObjectAnimator.ofFloat(binding.tvFullname, View.ALPHA, 1f).setDuration(500)
        val usernameET = ObjectAnimator.ofFloat(binding.etFullname, View.ALPHA, 1f).setDuration(500)
        val emailTV = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val emailET= ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val passwordTV = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val passwordET= ObjectAnimator.ofFloat(binding.etPassword, View.ALPHA, 1f).setDuration(500)
        val registerButton= ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)
        val haveAccount = ObjectAnimator.ofFloat(binding.tvHaveAccount, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(usernameTV, usernameET, emailTV, emailET,passwordTV,passwordET)
        }

        AnimatorSet().apply {
            playSequentially(
                title,
                together,
                registerButton,
                haveAccount
            )
            startDelay = 500
        }.start()
    }
}