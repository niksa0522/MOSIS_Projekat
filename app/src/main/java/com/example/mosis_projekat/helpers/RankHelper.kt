package com.example.mosis_projekat.helpers

object RankHelper {

    fun getRank(score:Int):String{
        return when{
            score<10 -> "No rank"
            score<50 -> "Bronze"
            score<100 -> "Silver"
            score<250 -> "Gold"
            else -> "Platinum"
        }
    }
}