package com.emeka45.universalcalculator.math
sealed interface Token {
    data class Number(val value:Double):Token
    data class Name(val value:String):Token
    data class Op(val value:String):Token
    data object Left:Token
    data object Right:Token
    data object Comma:Token
}
object Tokenizer {
    private val number=Regex("""\d+(?:\.\d*)?(?:[eE][+-]?\d+)?""")
    private val name=Regex("""[A-Za-z_][A-Za-z_0-9]*""")
    fun tokenize(s:String):List<Token>{
        val out=mutableListOf<Token>(); var i=0
        while(i<s.length){
            if(s[i].isWhitespace()){i++;continue}
            when(s[i]){
                '('->{out+=Token.Left;i++}; ')'->{out+=Token.Right;i++}; ','->{out+=Token.Comma;i++}
                '+','-','/','%','^','*','!'->{ if(s[i]=='*'&&i+1<s.length&&s[i+1]=='*'){out+=Token.Op("**");i+=2}else{out+=Token.Op(s[i].toString());i++} }
                else->{
                    val n=number.find(s,i); val a=name.find(s,i)
                    when { n!=null&&n.range.first==i->{out+=Token.Number(n.value.toDouble());i=n.range.last+1}
                        a!=null&&a.range.first==i->{out+=Token.Name(a.value.lowercase());i=a.range.last+1}
                        else->error("Unexpected character at ${i+1}") }
                }
            }
        }
        return out
    }
}
