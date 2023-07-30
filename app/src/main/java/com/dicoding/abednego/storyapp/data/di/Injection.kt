package com.dicoding.abednego.storyapp.data.di

import com.dicoding.abednego.storyapp.data.api.paging.StoryRepository
import com.dicoding.abednego.storyapp.data.api.retrofit.ApiConfig

object Injection {
    fun provideRepository(): StoryRepository {
        val apiService = ApiConfig().getApiService()
        return StoryRepository(apiService)
    }
}