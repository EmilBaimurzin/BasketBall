package com.basket.game.ui.options

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OptionsViewModel: ViewModel() {
    private val _currentBall = MutableLiveData(0)
    val currentBall: LiveData<Int> = _currentBall

    fun left() {
        if (_currentBall.value!! - 1 >= 1) {
            _currentBall.postValue(_currentBall.value!! - 1)
        }
    }

    fun right() {
        if (_currentBall.value!! + 1 <= 8) {
            _currentBall.postValue(_currentBall.value!! + 1)
        }
    }

    fun update() {
        _currentBall.postValue(_currentBall.value)
    }

    fun setBall(ball: Int) {
        _currentBall.postValue(ball)
    }
}