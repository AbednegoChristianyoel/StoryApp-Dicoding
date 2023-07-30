package com.dicoding.abednego.storyapp.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.abednego.storyapp.data.api.response.RegisterResponse
import com.dicoding.abednego.storyapp.data.api.retrofit.ApiConfig
import com.dicoding.abednego.storyapp.data.api.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    private val _registerResponse: MutableLiveData<String> = MutableLiveData()
    val registerResponse: LiveData<String> = _registerResponse

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val apiService: ApiService = ApiConfig().getApiService()

    fun userRegister(name: String, email: String, password: String) {
        _isLoading.value = true
        apiService.userRegister(name, email, password).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _registerResponse.postValue(response.message())
                } else {
                    _errorMessage.postValue(response.message())
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object{
        private const val TAG = "RegisterViewModel"
    }
}





