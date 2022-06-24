package com.example.mosis_projekat.models

import com.example.mosis_projekat.firebase.databaseModels.User
import com.example.mosis_projekat.firebase.databaseModels.Workshop

data class UserWithRank(var user: User, var id:String, var rank:Int)
