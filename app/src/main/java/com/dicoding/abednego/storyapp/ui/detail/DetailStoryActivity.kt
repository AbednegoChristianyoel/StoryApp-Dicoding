package com.dicoding.abednego.storyapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.dicoding.abednego.storyapp.R
import com.dicoding.abednego.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.title_detail_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val name = intent.getStringExtra(EXTRA_NAME)
        val photoUrl = intent.getStringExtra(EXTRA_PHOTO)
        val description = intent.getStringExtra(EXTRA_DESCRIPTION)

        binding.tvUsername.text = name
        Glide.with(this).load(photoUrl).into(binding.ivContent)
        binding.tvDescription.text = description
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

    companion object {
        const val EXTRA_NAME = "EXTRA_NAME"
        const val EXTRA_PHOTO = "EXTRA_PHOTO"
        const val EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION"
    }
}