package com.example.imagetotextandroidapp.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel(){
    val isSplashDone = MutableStateFlow(false)
    init {
        viewModelScope.launch{
            delay(2000)
            isSplashDone.value = true
        }
    }
}

