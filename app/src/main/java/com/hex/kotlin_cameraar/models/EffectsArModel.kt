package com.hex.kotlin_cameraar.models


data class EffectsArModel(
    val effectFile: String,
    val thumbnailFile: String
    )

class EffectProvider(){
    private val effects = listOf<EffectsArModel>(
        EffectsArModel("none","none"),
        EffectsArModel("Vendetta_Mask.deepar","Vendetta_Mask.png"),
        EffectsArModel("viking_helmet.deepar","viking_helmet.png"),
        EffectsArModel("flower_face.deepar","flower_face.png"),
    )


    fun getEffectFile(i: Int): EffectsArModel {
        return effects[i]
    }

    fun getEffectSizeList(): Int {
        return effects.size
    }
}