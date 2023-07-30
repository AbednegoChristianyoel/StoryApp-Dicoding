package com.dicoding.abednego.storyapp.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.abednego.storyapp.R
import com.dicoding.abednego.storyapp.data.datastore.UserPreferences
import com.dicoding.abednego.storyapp.databinding.ActivityHomeBinding
import com.dicoding.abednego.storyapp.ui.adapter.LoadingStateAdapter
import com.dicoding.abednego.storyapp.ui.adapter.StoriesAdapter
import com.dicoding.abednego.storyapp.ui.maps.MapsActivity
import com.dicoding.abednego.storyapp.ui.settings.SettingsActivity
import com.dicoding.abednego.storyapp.ui.upload.UploadActivity
import com.dicoding.abednego.storyapp.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var userPreferences: UserPreferences
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory()
    }
    private lateinit var storyAdapter: StoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences(applicationContext)

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        storyAdapter = StoriesAdapter()
        binding.rvStory.adapter = storyAdapter.withLoadStateHeaderAndFooter(
            header = LoadingStateAdapter { storyAdapter.retry() },
            footer = LoadingStateAdapter { storyAdapter.retry() }
        )

        setRvStories()

        userPreferences.isLogin.onEach { token ->
            if (token.isNotEmpty()) {
                getData(token)
            }
        }.launchIn(lifecycleScope)

        binding.swipeRefreshLayout.setOnRefreshListener {
            userPreferences.isLogin.onEach { token ->
                if (token.isNotEmpty()) {
                    getData(token)
                }
            }.launchIn(lifecycleScope)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.btnMap.setOnClickListener{
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getData(token: String) {
        binding.rvStory.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )
        homeViewModel.getStories(token).observe(this){
            storyAdapter.submitData(lifecycle, it)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        val addMenuItem = menu.findItem(R.id.add)
        addMenuItem.setOnMenuItemClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
            true
        }

        val settingsMenuItem = menu.findItem(R.id.settings)
        settingsMenuItem.setOnMenuItemClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }

        val refreshMenuItem = menu.findItem(R.id.refresh)
        refreshMenuItem.setOnMenuItemClickListener {
            userPreferences.isLogin.onEach { token ->
                if (token.isNotEmpty()) {
                    getData(token)
                }
            }.launchIn(lifecycleScope)
            true
        }
        return true
    }

    private fun setRvStories() {
        storyAdapter.addLoadStateListener { loadState ->
            if ((loadState.refresh is LoadState.NotLoading &&
                        loadState.append.endOfPaginationReached &&
                        storyAdapter.itemCount < 1) ||
                loadState.refresh is LoadState.Error) {
                binding.apply {
                    tvNotFoundError.visibility = View.VISIBLE
                    rvStory.visibility = View.GONE
                }
            } else {
                binding.apply {
                    tvNotFoundError.visibility = View.GONE
                    rvStory.visibility = View.VISIBLE
                }
            }
            binding.swipeRefreshLayout.isRefreshing = loadState.refresh is LoadState.Loading
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("finishAffinity()"))
    override fun onBackPressed() {
        finishAffinity()
    }
}