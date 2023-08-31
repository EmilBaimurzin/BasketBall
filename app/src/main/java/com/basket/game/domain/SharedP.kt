package com.basket.game.domain

import android.content.Context

class SharedP(private val context: Context) {
    private val prefs = context.getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)

    fun getBest(): Int = prefs.getInt("BEST", 0)
    fun setBest(best: Int) = prefs.edit().putInt("BEST", best).apply()

    fun getDiamonds() = prefs.getInt("DIAMONDS", 0)
    fun setDiamonds(diamonds: Int) =
        prefs.edit().putInt("DIAMONDS", getDiamonds() + diamonds).apply()

    fun getSelected() = prefs.getInt("SELECTED", 1)
    fun setSelected(selected: Int) = prefs.edit().putInt("SELECTED", selected).apply()

    fun isSymbolBought(symbol: Int): Boolean = prefs.getBoolean(symbol.toString(), false)
    fun buySymbol(symbol: Int) = prefs.edit().putBoolean(symbol.toString(), true).apply()
}