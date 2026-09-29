package com.emeka45.universalcalculator.math
import kotlin.math.abs
data class Matrix(val a:Array<DoubleArray>){
    val rows get()=a.size; val cols get()=if(a.isEmpty())0 else a[0].size
    operator fun times(o:Matrix):Matrix{require(cols==o.rows);return Matrix(Array(rows){i->DoubleArray(o.cols){j->(0 until cols).sumOf{k->a[i][k]*o.a[k][j]}}})}
    fun transpose()=Matrix(Array(cols){j->DoubleArray(rows){i->a[i][j]}})
    fun determinant():Double{require(rows==cols&&rows>0);val m=Array(rows){a[it].clone()};var d=1.0;for(i in 0 until rows){var p=i;for(r in i+1 until rows)if(abs(m[r][i])>abs(m[p][i]))p=r;if(abs(m[p][i])<1e-12)return 0.0;if(p!=i){val z=m[i];m[i]=m[p];m[p]=z;d=-d};d*=m[i][i];for(r in i+1 until rows){val f=m[r][i]/m[i][i];for(c in i+1 until rows)m[r][c]-=f*m[i][c]}};return d}
    fun inverse():Matrix{require(rows==cols&&rows>0);val n=rows;val m=Array(n){i->DoubleArray(2*n){j->if(j<n)a[i][j] else if(j-n==i)1.0 else 0.0}};for(i in 0 until n){var p=i;for(r in i+1 until n)if(abs(m[r][i])>abs(m[p][i]))p=r;require(abs(m[p][i])>1e-12){"Singular matrix"};val z=m[i];m[i]=m[p];m[p]=z;val d=m[i][i];for(c in 0 until 2*n)m[i][c]/=d;for(r in 0 until n)if(r!=i){val f=m[r][i];for(c in 0 until 2*n)m[r][c]-=f*m[i][c]}};return Matrix(Array(n){i->DoubleArray(n){j->m[i][j+n]}})}
}
