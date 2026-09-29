package com.emeka45.universalcalculator.math
import kotlin.math.*
class ExpressionEngine(private val angle:AngleMode=AngleMode.RAD,private val variables:Map<String,Complex> = emptyMap()){
    enum class AngleMode{RAD,DEG,GRAD}
    private lateinit var t:List<Token>; private var p=0
    fun evaluate(expression:String):Complex{t=Tokenizer.tokenize(expression);p=0;val r=expr();if(p!=t.size)error("Unexpected input");return r}
    private fun expr():Complex{var x=term();while(p<t.size){val op=(t[p] as? Token.Op)?.value?:break;if(op!="+"&&op!="-")break;p++;val y=term();x=if(op=="+")x+y else x-y};return x}
    private fun term():Complex{var x=power();while(p<t.size){val op=(t[p] as? Token.Op)?.value?:break;if(op!="*"&&op!="/"&&op!="%")break;p++;val y=power();x=when(op){"*"->x*y;"/"->x/y;else->Complex(x.re%y.re)}};return x}
    private fun power():Complex{var x=unary();if(p<t.size&&t[p] is Token.Op&&(t[p] as Token.Op).value in listOf("^","**")){p++;x=x.pow(power())};return x}
    private fun unary():Complex{if(p<t.size&&t[p] is Token.Op){when((t[p] as Token.Op).value){"+"->{p++;return unary()};"-"->{p++;return unary().neg()}}};var x=primary();while(p<t.size&&t[p] is Token.Op&&(t[p] as Token.Op).value=="!"){p++;x=Complex(factorial(x.re))};return x}
    private fun primary():Complex{
        if(p>=t.size)error("Incomplete expression")
        return when(val q=t[p++]){
            is Token.Number->Complex(q.value)
            is Token.Name->{if(p<t.size&&t[p] is Token.Left){p++;val a=mutableListOf<Complex>();if(p<t.size&&t[p]!is Token.Right){a+=expr();while(p<t.size&&t[p] is Token.Comma){p++;a+=expr()}};if(p>=t.size||t[p]!is Token.Right)error("Missing )");p++;function(q.value,a)}else variable(q.value)}
            Token.Left->{val x=expr();if(p>=t.size||t[p]!is Token.Right)error("Missing )");p++;x}
            else->error("Expected a number or function")
        }
    }
    private fun variable(n:String)=when(n){"pi"->Complex(PI);"e"->Complex(E);"i"->Complex(0.0,1.0);"tau"->Complex(2*PI);"phi"->Complex((1+sqrt(5.0))/2);else->variables[n]?:error("Unknown name: $n")}
    private fun function(n:String,a:List<Complex>):Complex{
        fun one()=a.singleOrNull()?:error("$n expects 1 argument")
        fun real(v:Complex)=if(abs(v.im)<1e-12)v.re else error("$n requires a real argument")
        fun trig(f:(Double)->Double):Complex{val x=real(one());return Complex(f(toRad(x)))}
        return when(n){
            "sin"->trig(::sin);"cos"->trig(::cos);"tan"->trig(::tan)
            "asin"->Complex(fromRad(asin(real(one()))));"acos"->Complex(fromRad(acos(real(one()))));"atan"->Complex(fromRad(atan(real(one()))))
            "sinh"->Complex(sinh(real(one())));"cosh"->Complex(cosh(real(one())));"tanh"->Complex(tanh(real(one())))
            "sqrt"->one().sqrtC();"abs"->Complex(one().abs());"arg"->Complex(one().arg());"conj"->one().conj()
            "exp"->{val x=one();Complex(exp(x.re)*cos(x.im),exp(x.re)*sin(x.im))}
            "ln"->{val x=one();Complex(ln(x.abs()),x.arg())}
            "log","log10"->Complex(log10(real(one())));"log2"->Complex(log2(real(one())))
            "floor"->Complex(floor(real(one())));"ceil"->Complex(ceil(real(one())));"round"->Complex(round(real(one())))
            "deg"->Complex(fromRad(real(one())));"rad"->Complex(toRad(real(one())))
            "fact","factorial"->Complex(factorial(real(one())))
            "ncr"->Complex(combination(real(a[0]),real(a[1])));"npr"->Complex(permutation(real(a[0]),real(a[1])))
            "gcd"->Complex(gcd(real(a[0]).toLong(),real(a[1]).toLong()).toDouble())
            "lcm"->Complex(lcm(real(a[0]).toLong(),real(a[1]).toLong()).toDouble())
            else->error("Unknown function: $n")
        }
    }
    private fun toRad(x:Double)=when(angle){AngleMode.RAD->x;AngleMode.DEG->Math.toRadians(x);AngleMode.GRAD->x*PI/200}
    private fun fromRad(x:Double)=when(angle){AngleMode.RAD->x;AngleMode.DEG->Math.toDegrees(x);AngleMode.GRAD->x*200/PI}
    companion object{
        fun factorial(x:Double):Double{require(x>=0&&x<=170&&x%1.0==0.0){"Factorial requires an integer 0..170"};var r=1.0;for(i in 2..x.toInt())r*=i;return r}
        fun permutation(n:Double,r:Double)=factorial(n)/factorial(n-r)
        fun combination(n:Double,r:Double)=permutation(n,r)/factorial(r)
        fun gcd(a0:Long,b0:Long):Long{var a=abs(a0);var b=abs(b0);while(b!=0L){val z=a%b;a=b;b=z};return a}
        fun lcm(a:Long,b:Long)=if(a==0L||b==0L)0L else abs(a/gcd(a,b)*b)
    }
}
