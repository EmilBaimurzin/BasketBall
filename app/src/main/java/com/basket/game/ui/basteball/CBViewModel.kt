package com.basket.game.ui.basteball

import androidx.lifecycle.ViewModel

class CBViewModel: ViewModel() {
    var callback: (() -> Unit)? = null
}