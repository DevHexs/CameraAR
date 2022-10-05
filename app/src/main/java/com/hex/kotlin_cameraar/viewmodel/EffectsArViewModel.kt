package com.hex.kotlin_cameraar.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.hex.kotlin_cameraar.models.EffectProvider

class EffectsArViewModel : ViewModel() {

    private val effectProvider = EffectProvider()
    private val effectTotal = effectProvider.getEffectSizeList()

    private fun getFilterPath(filterName: String): String {
        return if (filterName == "none")
            "none"
        else
            "file:///android_asset/$filterName"
    }

    fun changeEffectsAR(i: Int) : ArrayList<String> {
        val paths = ArrayList<String>()
        Log.i("Index", i.toString())
        val effect = effectProvider.getEffectFile(i)
        paths.add(getFilterPath(effect.effectFile))
        paths.add(effect.thumbnailFile)
        return paths
    }

    fun getEffectsSize(): Int {
        return effectTotal
    }
}