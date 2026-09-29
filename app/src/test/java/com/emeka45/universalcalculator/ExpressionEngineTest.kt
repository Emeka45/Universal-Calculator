package com.emeka45.universalcalculator
import com.emeka45.universalcalculator.math.ExpressionEngine
import org.junit.Assert.assertEquals
import org.junit.Test
class ExpressionEngineTest{
    private val e=ExpressionEngine()
    @Test fun precedence()=assertEquals(14.0,e.evaluate("2+3*4").re,1e-12)
    @Test fun parentheses()=assertEquals(20.0,e.evaluate("(2+3)*4").re,1e-12)
    @Test fun trig()=assertEquals(1.0,e.evaluate("sin(pi/2)").re,1e-12)
    @Test fun degrees()=assertEquals(1.0,ExpressionEngine(ExpressionEngine.AngleMode.DEG).evaluate("sin(90)").re,1e-12)
    @Test fun factorial()=assertEquals(120.0,e.evaluate("5!").re,1e-12)
    @Test fun complex() { val z=e.evaluate("(2+3*i)*(2-3*i)");assertEquals(13.0,z.re,1e-12);assertEquals(0.0,z.im,1e-12) }
}
