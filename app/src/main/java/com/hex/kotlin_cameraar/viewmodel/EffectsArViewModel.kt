package com.hex.kotlin_cameraar.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.hex.kotlin_cameraar.models.EffectProvider

class EffectsArViewModel : ViewModel() {

    private val effectProvider = EffectProvider()
    private val effectTotal = effectProvider.getEffectSizeList()

    private fun getFilterPath(filterName: String): String? {
        return if (filterName == "none")
            null
        else
            "file:///android_asset/$filterName"
    }

    fun changeEffectsAR(i: Int) : String? {
        val effectName = effectProvider.getEffectFile(i).effectFile
        Log.d("Effect",effectName)
        return getFilterPath(effectName)
    }

    fun getEffectsSize(): Int {
        return effectTotal
    }
}