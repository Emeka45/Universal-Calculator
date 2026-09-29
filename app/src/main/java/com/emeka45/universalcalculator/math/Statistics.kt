package com.emeka45.universalcalculator.math
import kotlin.math.sqrt
data class StatisticsResult(val count:Int,val sum:Double,val mean:Double,val median:Double,val variance:Double,val standardDeviation:Double,val min:Double,val max:Double)
object Statistics{fun describe(values:List<Double>):StatisticsResult{require(values.isNotEmpty());val s=values.sorted();val mean=s.average();val variance=s.sumOf{(it-mean)*(it-mean)}/s.size;return StatisticsResult(s.size,s.sum(),mean,if(s.size%2==1)s[s.size/2] else(s[s.size/2-1]+s[s.size/2])/2,variance,sqrt(variance),s.first(),s.last())}}
