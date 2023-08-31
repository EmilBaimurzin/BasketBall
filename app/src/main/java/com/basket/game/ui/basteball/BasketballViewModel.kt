package com.basket.game.ui.basteball

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basket.game.core.library.XYImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BasketballViewModel : ViewModel() {
    private var gameScope = CoroutineScope(Dispatchers.IO)
    private val _basketXY = MutableLiveData(XYImpl(0f, 0f))
    val basketXY: LiveData<XYImpl> = _basketXY

    private val _ballXY = MutableLiveData<XYImpl?>(null)
    val ballXY: LiveData<XYImpl?> = _ballXY

    private val _energy = MutableLiveData(13)
    val energy: LiveData<Int> = _energy

    private val _scores = MutableLiveData(0)
    val scores: LiveData<Int> = _scores

    private var basketWeb = XYImpl(0f, 0f)

    var gameState = true
    var pauseState = false
    var isIntersected = false
    var canMove = true

    private var basketScopeRight = CoroutineScope(Dispatchers.IO)
    private var basketScopeLeft = CoroutineScope(Dispatchers.IO)
    private var isBasketMovingRight = true

    fun initBasket(x: Float, y: Float) {
        _basketXY.postValue(XYImpl(x, y))
    }

    fun start(
        basketWidth: Int,
        maxX: Float,
        minX: Float,
        ballSize: Int,
        webWidth: Int,
        webHeight: Int,
        maxY: Int
    ) {
        gameScope = CoroutineScope(Dispatchers.IO)
        basketScopeLeft = CoroutineScope(Dispatchers.IO)
        basketScopeRight = CoroutineScope(Dispatchers.IO)
        isIntersected = false
        canMove = true
        if (isBasketMovingRight) {
            moveCannonRight(basketWidth, maxX, minX)
        } else {
            moveCannonLeft(basketWidth, maxX, minX)
        }
        letBallFall(ballSize, webWidth, webHeight, maxY)
        startEnergy()
    }

    fun stop() {
        gameScope.cancel()
        basketScopeLeft.cancel()
        basketScopeRight.cancel()
    }

    private fun startEnergy() {
        gameScope.launch {
            while (true) {
                delay(1000)
                if (_energy.value!! - 1 >= 0) {
                    _energy.postValue(_energy.value!! - 1)
                }
            }
        }
    }

    fun setBasketWeb(x: Float, y: Float) {
        basketWeb = XYImpl(x, y)
    }

    fun spawnBall(x: Float, y: Float) {
        _ballXY.postValue(XYImpl(x, y))
    }

    private fun letBallFall(ballSize: Int, webWidth: Int, webHeight: Int, maxY: Int) {
        gameScope.launch {
            while (true) {
                delay(16)
                if (_ballXY.value != null) {
                    val newXy = XYImpl(x = _ballXY.value!!.x, _ballXY.value!!.y + 10)

                    val ballX = newXy.x.toInt()..(newXy.x + ballSize).toInt()
                    val ballY = newXy.y.toInt()..(newXy.y + ballSize).toInt()

                    val webX = basketWeb.x.toInt()..(basketWeb.x + webWidth).toInt()
                    val webY = basketWeb.y.toInt()..(basketWeb.y + webHeight).toInt()

                    if (ballX.any { it in webX } && ballY.any { it in webY } && !isIntersected) {
                        viewModelScope.launch {
                            canMove = false
                            delay(500)
                            canMove = true
                        }
                        isIntersected = true
                        if (newXy.x.toInt() + ballSize / 2 in webX) {
                            newXy.x = basketWeb.x + ((webWidth - ballSize) / 2)
                            _scores.postValue(_scores.value!! + 1)
                            _energy.postValue(13)
                        } else if (newXy.x.toInt() + ballSize / 2 < basketWeb.x) {
                            newXy.x = basketWeb.x - ballSize
                        } else {
                            newXy.x = basketWeb.x + webWidth
                        }
                    }
                    _ballXY.postValue(newXy)

                    if (newXy.y.toInt() >= maxY) {
                        isIntersected = false
                        _ballXY.postValue(null)
                    }
                }
            }
        }
    }

    private fun moveCannonRight(basketWidth: Int, maxX: Float, minX: Float) {
        basketScopeRight = CoroutineScope(Dispatchers.Default)
        basketScopeRight.launch {
            while (true) {
                delay(16)
                if (canMove) {
                    if (_basketXY.value!!.x + 10 + basketWidth > maxX) {
                        moveCannonLeft(basketWidth, maxX, minX)
                        isBasketMovingRight = false
                        basketScopeRight.cancel()
                    } else {
                        _basketXY.postValue(XYImpl(_basketXY.value!!.x + 10, _basketXY.value!!.y))
                    }
                }
            }
        }
    }

    private fun moveCannonLeft(basketWidth: Int, maxX: Float, minX: Float) {
        basketScopeLeft = CoroutineScope(Dispatchers.Default)
        basketScopeLeft.launch {
            while (true) {
                delay(16)
                if (canMove) {
                    if (_basketXY.value!!.x - 10 < minX) {
                        moveCannonRight(basketWidth, maxX, minX)
                        isBasketMovingRight = true
                        basketScopeLeft.cancel()
                    } else {
                        _basketXY.postValue(XYImpl(_basketXY.value!!.x - 10, _basketXY.value!!.y))
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}