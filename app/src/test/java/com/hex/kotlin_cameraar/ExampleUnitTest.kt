package com.hex.kotlin_cameraar

import com.hex.kotlin_cameraar.models.EffectProvider
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest() {

    private val effect = EffectProvider()
    private val effectTotal = effect.getEffectSizeList() - 1

    @Test
    fun getEffectFileTest() {
        println(effect.getEffectFile(1))
        println(effect.getEffectFile(2))
        println(effect.getEffectFile(3))
    }

    @Test
    fun getEffectSizeTest(){
        println(effect.getEffectSizeList())
    }

    @Test
    fun getEffectFilePathTest(){
        println(effect.getEffectFile(1).effectFile)
    }

    @Test
    fun changeEffectsAR() {
        val paths = ArrayList<String>()
        val effect = effect.getEffectFile(1)
        paths.add(getFilterPath(effect.effectFile)!!)
        paths.add(getFilterPath(effect.thumbnailFile)!!)
        println(paths)
    }

    private fun getFilterPath(filterName: String): String? {
        return if (filterName == "none")
            null
        else
            "file:///android_asset/$filterName"
    }

}