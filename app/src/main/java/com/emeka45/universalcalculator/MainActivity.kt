package com.emeka45.universalcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emeka45.universalcalculator.math.Complex
import com.emeka45.universalcalculator.math.ExpressionEngine
import com.emeka45.universalcalculator.math.Statistics
import kotlin.math.abs

private enum class Mode { BASIC, SCIENTIFIC, TOOLS, GRAPH }
private enum class Angle { RAD, DEG, GRAD }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CalculatorApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalculatorApp() {
    var mode by rememberSaveable { mutableStateOf(Mode.SCIENTIFIC) }
    var angle by rememberSaveable { mutableStateOf(Angle.RAD) }
    var expression by rememberSaveable { mutableStateOf("") }
    var result by rememberSaveable { mutableStateOf("") }
    var memory by rememberSaveable { mutableStateOf(0.0) }
    var dark by rememberSaveable { mutableStateOf(false) }
    val history = remember { mutableStateListOf<Pair<String,String>>() }

    MaterialTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme()) {
        Scaffold(topBar = {
            TopAppBar(title = { Text("Universal Calculator", fontWeight = FontWeight.Bold) },
                actions = { TextButton(onClick = { dark = !dark }) { Text(if (dark) "Light" else "Dark") } })
        }) { pad ->
            Column(Modifier.padding(pad).fillMaxSize().padding(10.dp)) {
                ModeBar(mode, { mode = it }, angle, { angle = it })
                Display(expression, result)
                when (mode) {
                    Mode.TOOLS -> ToolsPanel()
                    Mode.GRAPH -> GraphPanel()
                    else -> {
                        CalculatorPad(mode == Mode.SCIENTIFIC, expression, { expression = it }, {
                            try {
                                val c = ExpressionEngine(ExpressionEngine.AngleMode.valueOf(angle.name)).evaluate(it)
                                result = format(c)
                                if (history.firstOrNull()?.first != it) history.add(0, it to result)
                            } catch (e: Exception) {
                                result = "Error: " + (e.message ?: "Invalid expression")
                            }
                        }, memory, { memory = it })
                        if (history.isNotEmpty()) {
                            Text("History", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                            LazyColumn(Modifier.weight(1f)) {
                                items(history.take(12)) { (q, a) ->
                                    ListItem(headlineContent = { Text(q, fontFamily = FontFamily.Monospace) },
                                        supportingContent = { Text(a) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeBar(mode: Mode, onMode: (Mode) -> Unit, angle: Angle, onAngle: (Angle) -> Unit) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically) {
        Mode.values().forEach { m ->
            FilterChip(selected = mode == m, onClick = { onMode(m) },
                label = { Text(m.name.lowercase().replaceFirstChar { it.uppercase() }) },
                modifier = Modifier.padding(end = 4.dp))
        }
        Angle.values().forEach { a ->
            FilterChip(selected = angle == a, onClick = { onAngle(a) },
                label = { Text(a.name) }, modifier = Modifier.padding(end = 4.dp))
        }
    }
}

@Composable
private fun Display(expression: String, result: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(if (expression.isBlank()) "0" else expression, fontSize = 22.sp,
            modifier = Modifier.horizontalScroll(rememberScrollState()))
        Text(if (result.isBlank()) "0" else result, fontSize = 38.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.horizontalScroll(rememberScrollState()))
    }
}

@Composable
private fun CalculatorPad(
    scientific: Boolean,
    expression: String,
    setExpression: (String) -> Unit,
    calculate: (String) -> Unit,
    memory: Double,
    setMemory: (Double) -> Unit
) {
    val rows = mutableListOf<List<String>>()
    if (scientific) {
        rows += listOf("sin(", "cos(", "tan(", "ln(", "log(")
        rows += listOf("asin(", "acos(", "atan(", "sqrt(", "^")
        rows += listOf("sinh(", "cosh(", "tanh(", "!", "pi")
        rows += listOf("ncr(", "npr(", "gcd(", "lcm(", "i")
    }
    rows += listOf("7", "8", "9", "/", "%")
    rows += listOf("4", "5", "6", "*", "(")
    rows += listOf("1", "2", "3", "-", ")")
    rows += listOf("0", ".", "00", "+", "⌫")
    rows += listOf("C", "M+", "MR", "M-", "=")

    Column(Modifier.fillMaxWidth()) {
        rows.forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { key ->
                    Button(onClick = {
                        when (key) {
                            "=" -> if (expression.isNotBlank()) calculate(expression)
                            "C" -> setExpression("")
                            "⌫" -> setExpression(expression.dropLast(1))
                            "M+" -> setMemory(memory + (expression.toDoubleOrNull() ?: 0.0))
                            "M-" -> setMemory(memory - (expression.toDoubleOrNull() ?: 0.0))
                            "MR" -> setExpression(expression + memory.toString())
                            "pi" -> setExpression(expression + "pi")
                            else -> setExpression(expression + key)
                        }
                    }, modifier = Modifier.weight(1f).padding(2.dp).height(50.dp)) { Text(key, fontSize = 15.sp) }
                }
            }
        }
    }
}

@Composable
private fun ToolsPanel() {
    var data by rememberSaveable { mutableStateOf("") }
    var output by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Text("Mathematics tools", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Statistics and analysis workspace")
        OutlinedTextField(data, { data = it }, label = { Text("Data, comma separated") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            try {
                val values = data.split(",").filter { it.isNotBlank() }.map { it.trim().toDouble() }
                val s = Statistics.describe(values)
                output = "n=${s.count}\nsum=${s.sum}\nmean=${s.mean}\nmedian=${s.median}\nvariance=${s.variance}\nSD=${s.standardDeviation}\nmin=${s.min}\nmax=${s.max}"
            } catch (e: Exception) { output = "Error: " + (e.message ?: "Invalid data") }
        }, modifier = Modifier.padding(top = 8.dp)) { Text("Analyze") }
        Text(output, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
private fun GraphPanel() {
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Text("Graphing", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Expression plotting foundation. Use x as the independent variable.")
        Text("Examples: x^2, sin(x), 2*x+1")
        Text("Interactive plotting is isolated here so it can be expanded without changing calculation semantics.")
    }
}

private fun format(c: Complex): String {
    fun f(x: Double): String = when {
        !x.isFinite() -> x.toString()
        abs(x) < 1e-12 -> "0"
        x == x.toLong().toDouble() -> x.toLong().toString()
        else -> "%.12g".format(x)
    }
    return if (abs(c.im) < 1e-10) f(c.re)
    else f(c.re) + if (c.im >= 0) " + " + f(c.im) + "i" else " - " + f(abs(c.im)) + "i"
}
