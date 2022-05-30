package com.example.mosis_projekat.databaseModels

import java.util.*

data class Workshop(
    var avgRating: Float = 0f,
    var numRatings: Int = 0,
    var location: DatabaseLocation,
    var name: String? = null,
    var description:String? = null,
    var type:String? = null,
    var phone: String? = null,
    var price: Float = 0f,
    var imageUrl: String? = null,
    var uploaderUID: String? = null
){

    fun getKeywords(): List<String>? {
        if(name!=null) {
            val words: Array<String> = name!!.split("\\s+").toTypedArray()
            val capitalWords: MutableList<String> = java.util.ArrayList()
            for (s in words) {
                val cw = s.split("(?=\\p{Upper})").toTypedArray()
                for (i in cw.indices) {
                    cw[i] = cw[i].toLowerCase()
                }
                capitalWords.addAll(Arrays.asList(*cw))
            }
            for (i in 1 until name!!.length) {
                var str: String = name!!.substring(0, i)
                str = str.toLowerCase()
                capitalWords.add(str)
            }
            capitalWords.add(name!!.toLowerCase())
            return capitalWords
        }
        return null
    }
}
