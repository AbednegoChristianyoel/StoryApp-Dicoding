package com.dicoding.abednego.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.abednego.storyapp.data.api.response.LoginResponse
import com.dicoding.abednego.storyapp.data.api.retrofit.ApiConfig
import com.dicoding.abednego.storyapp.data.api.retrofit.ApiService
import com.dicoding.abednego.storyapp.data.datastore.UserPreferences
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    private val _loginSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val apiService: ApiService = ApiConfig().getApiService()

    fun userLogin(email: String, password: String) {
        _isLoading.value = true
        apiService.userLogin(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val loginResponse = response.body()?.loginResult
                    val userToken = loginResponse?.token
                    viewModelScope.launch {
                        userPreferences.saveUserToken(userToken)
                    }
                    _loginSuccess.value = true
                } else {
                    _errorMessage.postValue(response.message())
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object{
        private const val TAG = "LoginViewModel"
    }
}