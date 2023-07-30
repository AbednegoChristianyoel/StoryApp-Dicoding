package com.dicoding.abednego.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.dicoding.abednego.storyapp.ui.home.HomeActivity
import com.dicoding.abednego.storyapp.R
import com.dicoding.abednego.storyapp.data.datastore.UserPreferences
import com.dicoding.abednego.storyapp.ui.register.RegisterActivity
import com.dicoding.abednego.storyapp.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        userPreferences = UserPreferences(applicationContext)
        viewModel = LoginViewModel(userPreferences)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setLoginButtonEnable()

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

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.userLogin(email, password)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.loginSuccess.observe(this) { response ->
            if (response != null) {
                Toast.makeText(this, getString(R.string.toast_login_success), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message != null) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.toast_login_error_message),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        binding.tvDontHaveAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        playAnimation()
    }

    private fun playAnimation() {

        val title = ObjectAnimator.ofFloat(binding.tvLoginTitle, View.ALPHA, 1f).setDuration(500)
        val emailTV = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val emailET= ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val passwordTV = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val passwordET= ObjectAnimator.ofFloat(binding.etPassword, View.ALPHA, 1f).setDuration(500)
        val forgotTV = ObjectAnimator.ofFloat(binding.tvForgotPassword, View.ALPHA, 1f).setDuration(500)
        val loginButton= ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val noAccount = ObjectAnimator.ofFloat(binding.tvDontHaveAccount, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(emailTV, emailET,passwordTV,passwordET)
        }

        AnimatorSet().apply {
            playSequentially(
                title,
                together,
                forgotTV,
                loginButton,
                noAccount
            )
            startDelay = 500
        }.start()
    }

    private fun setLoginButtonEnable() {
        val emailState = binding.etEmail.text.toString().isNotEmpty()
        val passwordState = binding.etPassword.text.toString().isNotEmpty() && binding.etPassword.error.isNullOrEmpty()
        binding.btnLogin.isEnabled = emailState && passwordState
    }
}