package com.dicoding.abednego.storyapp.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.abednego.storyapp.data.api.response.ListStoryItem
import com.dicoding.abednego.storyapp.data.api.response.StoriesResponse
import com.dicoding.abednego.storyapp.data.api.retrofit.ApiConfig
import com.dicoding.abednego.storyapp.data.api.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel : ViewModel() {

    private var _storyList = MutableLiveData<List<ListStoryItem>>()
    val storyList: LiveData<List<ListStoryItem>> = _storyList

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> = _errorMessage

    private val apiService: ApiService = ApiConfig().getApiService()

    fun getAllStories(token: String) {
        val getToken = generateBearerToken(token)
        apiService.getLocation(getToken,location = 1).enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(call: Call<StoriesResponse>, response: Response<StoriesResponse>) {
                if (response.isSuccessful) {
                    _storyList.value = response.body()?.listStory
                } else {
                    _errorMessage.postValue(response.message())
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    private fun generateBearerToken(token: String): String {
        return "Bearer $token"
    }
    companion object{
        private const val TAG = "HomeViewModel"
    }
}