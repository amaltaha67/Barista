package com.example.barista

import java.io.Serializable


data class Recipe(val id : Int,
                  val title : String,
                  val image : String,
                  val description : String,
                  val date : String) : Serializable