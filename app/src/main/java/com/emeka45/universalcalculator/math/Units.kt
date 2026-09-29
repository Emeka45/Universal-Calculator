package com.emeka45.universalcalculator.math
object Units{
    private val f=mapOf("m" to 1.0,"km" to 1000.0,"cm" to .01,"mm" to .001,"mi" to 1609.344,"ft" to .3048,"in" to .0254,"kg" to 1.0,"g" to .001,"lb" to .45359237,"oz" to .028349523125,"s" to 1.0,"min" to 60.0,"h" to 3600.0,"day" to 86400.0)
    fun convert(v:Double,from:String,to:String):Double{require(from in f&&to in f){"Unsupported units"};return v*f.getValue(from)/f.getValue(to)}
}
