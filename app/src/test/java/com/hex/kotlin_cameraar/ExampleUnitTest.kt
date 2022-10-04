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
        for (i in 0..effectTotal){
            val effectName = effect.getEffectFile(i).effectFile
            println("file:///android_asset/$effectName")
        }
    }

}