package com.dicoding.abednego.storyapp.data.api.paging

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.abednego.storyapp.data.api.response.ListStoryItem
import com.dicoding.abednego.storyapp.data.api.retrofit.ApiService

class StoryRepository(private val apiService: ApiService) {
    fun getAllStories(token: String): LiveData<PagingData<ListStoryItem>> {
        val getToken = generateBearerToken(token)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService,getToken)
            }
        ).liveData
    }

    private fun generateBearerToken(token: String): String {
        return "Bearer $token"
    }
}