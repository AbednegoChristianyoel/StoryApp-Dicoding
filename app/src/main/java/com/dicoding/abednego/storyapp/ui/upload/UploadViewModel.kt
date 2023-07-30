package com.dicoding.abednego.storyapp.ui.upload

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.abednego.storyapp.data.api.response.UploadResponse
import com.dicoding.abednego.storyapp.data.api.retrofit.ApiConfig
import com.dicoding.abednego.storyapp.data.api.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadViewModel: ViewModel() {

    private val _uploadResponse: MutableLiveData<String> = MutableLiveData()
    val uploadResponse: LiveData<String> = _uploadResponse

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val apiService: ApiService = ApiConfig().getApiService()

    fun uploadStory(token: String, image: MultipartBody.Part, description: RequestBody, latitude: RequestBody? = null, longitude: RequestBody? = null) {
        _isLoading.value = true
        val getToken = generateBearerToken(token)
        apiService.uploadStory(getToken, image, description,latitude,longitude).enqueue(object : Callback<UploadResponse> {
            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _uploadResponse.postValue(response.message())
                } else {
                    _errorMessage.postValue(response.message())
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    private fun generateBearerToken(token: String): String {
        return "Bearer $token"
    }
    companion object{
        private const val TAG = "UploadViewModel"
    }
}