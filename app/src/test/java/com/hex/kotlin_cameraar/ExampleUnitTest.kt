package com.hex.kotlin_cameraar

import com.hex.kotlin_cameraar.models.EffectProvider
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest() {


    @Test
    fun getEffectFileTest() {
        val effects = EffectProvider()
        println(effects.getEffectFile(1))
        println(effects.getEffectFile(2))
        println(effects.getEffectFile(3))
    }

    @Test
    fun getEffectSizeTest(){
        val effects= EffectProvider()
        println(effects.getEffectSizeList())
    }

    @Test
    fun getEffectFilePathTest(){
        val effects = EffectProvider()
        println(effects.getEffectFile(0).effectFile)
    }

    @Test
    fun changeEffectsAR() {
        val effects = EffectProvider()
        val paths = ArrayList<String>()
        //val effects = effect.getEffectFile(0)
        paths.add(getFilterPath(effects.getEffectFile(0).effectFile))
        paths.add(getFilterPath(effects.getEffectFile(0).thumbnailFile))
        println(paths)
    }

    private fun getFilterPath(filterName: String): String {
        return if (filterName == "none")
            "none"
        else
            "file:///android_asset/$filterName"
    }

}