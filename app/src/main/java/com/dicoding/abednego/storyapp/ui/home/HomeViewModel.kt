package com.dicoding.abednego.storyapp.ui.home

import androidx.lifecycle.*
import androidx.paging.*
import com.dicoding.abednego.storyapp.data.api.paging.StoryRepository
import com.dicoding.abednego.storyapp.data.api.response.ListStoryItem

class HomeViewModel (private val repository: StoryRepository): ViewModel() {
    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> =
        repository.getAllStories(token).cachedIn(viewModelScope)
}