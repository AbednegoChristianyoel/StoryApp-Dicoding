package com.dicoding.abednego.storyapp.ui.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.dicoding.abednego.storyapp.R
import com.dicoding.abednego.storyapp.data.datastore.UserPreferences
import com.dicoding.abednego.storyapp.databinding.ActivitySettingsBinding
import com.dicoding.abednego.storyapp.ui.login.LoginActivity
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.title_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userPreferences = UserPreferences(applicationContext)

        binding.btnLanguageSetting.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirmation))
                .setMessage(getString(R.string.are_you_sure_log_out))
                .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    dialog.dismiss()
                    lifecycleScope.launch {
                        userPreferences.clearUserToken()
                        val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}