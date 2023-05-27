package com.example.myapplication.classes
//the other way using data classes the first way used was mutableMaps with key value pairs

data class Note(val title:String ,val description:String)
{
  constructor():this("","")//public no arg constructor needed otherwise app will crash compiler tells u
}