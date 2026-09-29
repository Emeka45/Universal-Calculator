package com.emeka45.universalcalculator.math
import kotlin.math.*
data class Complex(val re: Double, val im: Double = 0.0) {
    operator fun plus(o: Complex)=Complex(re+o.re,im+o.im)
    operator fun minus(o: Complex)=Complex(re-o.re,im-o.im)
    operator fun times(o: Complex)=Complex(re*o.re-im*o.im,re*o.im+im*o.re)
    operator fun div(o: Complex):Complex { val d=o.re*o.re+o.im*o.im; require(d!=0.0){"Division by zero"}; return Complex((re*o.re+im*o.im)/d,(im*o.re-re*o.im)/d) }
    fun neg()=Complex(-re,-im)
    fun abs()=hypot(re,im)
    fun arg()=atan2(im,re)
    fun conj()=Complex(re,-im)
    fun pow(o:Complex):Complex { if(re==0.0&&im==0.0)return Complex(0.0); val r=abs().pow(o.re)*exp(-o.im*arg()); val t=o.re*arg()+o.im*ln(abs()); return Complex(r*cos(t),r*sin(t)) }
    fun sqrtC():Complex { val r=sqrt(abs()); val t=arg()/2; return Complex(r*cos(t),r*sin(t)) }
}
