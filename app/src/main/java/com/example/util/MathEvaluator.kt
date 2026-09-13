package com.example.util

import java.util.Locale
import kotlin.math.*

object MathEvaluator {

    /**
     * Strict evaluation for authoritative calculation (e.g. when pressing "=").
     * Rejects trailing operators, unclosed parentheses, incomplete functions, unknown characters, etc.
     */
    fun evaluateStrict(
        expression: String,
        isDegreeMode: Boolean = true,
        precision: Int = -1,
        formatStyle: String = "STANDARD"
    ): EvaluationResult {
        val trimmed = expression.trim()
        if (trimmed.isBlank()) {
            return EvaluationResult.Error("Empty Expression")
        }

        // Check for trailing binary operators
        val rawClean = trimmed.replace("×", "*").replace("÷", "/").replace("−", "-").trim()
        if (rawClean.endsWith("+") || rawClean.endsWith("-") || rawClean.endsWith("*") ||
            rawClean.endsWith("/") || rawClean.endsWith("^") || rawClean.endsWith("(")
        ) {
            return EvaluationResult.Error("Incomplete expression")
        }

        try {
            val sanitized = sanitizeExpression(trimmed, formatStyle, strict = true)
            val tokens = tokenize(sanitized)
            val rpn = shuntingYard(tokens)
            val resultValue = evalRPN(rpn, isDegreeMode)

            if (resultValue.isNaN()) {
                return EvaluationResult.Error("Undefined Result")
            }
            if (resultValue.isInfinite()) {
                return EvaluationResult.Error("Division by Zero / Infinity")
            }

            val formatted = formatNumber(resultValue, precision, formatStyle)
            return EvaluationResult.Success(resultValue, formatted)
        } catch (e: Exception) {
            return EvaluationResult.Error(e.message ?: "Invalid Syntax")
        }
    }

    /**
     * Standard evaluate function; defaults to strict evaluation to prevent silent truncation.
     */
    fun evaluate(
        expression: String,
        isDegreeMode: Boolean = true,
        precision: Int = -1,
        formatStyle: String = "STANDARD"
    ): EvaluationResult {
        return evaluateStrict(expression, isDegreeMode, precision, formatStyle)
    }

    /**
     * Evaluates live expression preview. Tolerates incomplete expressions (trailing operators, unclosed parentheses).
     */
    fun evaluatePartial(
        expression: String,
        isDegreeMode: Boolean = true,
        precision: Int = -1,
        formatStyle: String = "STANDARD"
    ): EvaluationResult {
        if (expression.isBlank()) return EvaluationResult.Success(0.0, "0")

        // 1. First attempt strict evaluation
        val strictRes = evaluateStrict(expression, isDegreeMode, precision, formatStyle)
        if (strictRes is EvaluationResult.Success) {
            return strictRes
        }

        // 2. Iteratively trim trailing operators/invalid tokens for preview
        var expr = expression.trim()
        val visited = mutableSetOf<String>()

        while (expr.isNotEmpty() && visited.add(expr)) {
            val trimmed = expr.dropLast(1).trim()
            if (trimmed.isEmpty()) break
            expr = trimmed

            val res = evaluateStrict(expr, isDegreeMode, precision, formatStyle)
            if (res is EvaluationResult.Success) {
                return res
            }

            // Auto-close missing trailing parentheses for function calls (e.g. "sin(30" -> "sin(30)")
            val openCount = expr.count { it == '(' }
            val closeCount = expr.count { it == ')' }
            if (openCount > closeCount) {
                val autoClosed = expr + ")".repeat(openCount - closeCount)
                val closedRes = evaluateStrict(autoClosed, isDegreeMode, precision, formatStyle)
                if (closedRes is EvaluationResult.Success) {
                    return closedRes
                }
            }
        }

        return strictRes
    }

    private fun sanitizeExpression(expr: String, formatStyle: String = "STANDARD", strict: Boolean = false): String {
        var s = expr.replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "pi")
            .replace(" ", "")
            .replace("\u00A0", "")
            .trim()

        // Clean thousands separators / decimals according to formatStyle
        if (formatStyle.equals("EUROPEAN", ignoreCase = true)) {
            // In European format, '.' is thousands separator (e.g. 1.000) and ',' is decimal (e.g. 3,14)
            s = s.replace(Regex("(?<=\\d)\\.(?=\\d)"), "") // Strip thousands separator dot
            s = s.replace(',', '.') // Replace decimal comma with standard dot for evaluation
        } else {
            // In Standard/Indian/Plain format, ',' is thousands separator (e.g. 1,000 or 1,00,000)
            s = s.replace(",", "") // Strip all thousands commas
        }

        s = preprocessPercentages(s)

        // Insert implicit multiplication: e.g. 2pi -> 2*pi, 3( -> 3*(, )4 -> )*4, pi( -> pi*(, 5sin -> 5*sin, )( -> )*(
        // Note: Do NOT match 'e' if it is part of scientific notation like 1e3
        val implicitRegexes = listOf(
            Regex("(\\d|\\)|pi)(pi|\\(|sin|cos|tan|asin|acos|atan|sinh|cosh|tanh|asinh|acosh|atanh|log|ln|sqrt|abs)") to "$1*$2",
            Regex("(?<=[0-9])e(?![0-9+\\-])") to "*e",
            Regex("(\\)|pi)(\\d)") to "$1*$2",
            Regex("(\\))(\\()") to "$1*$2"
        )

        for ((regex, replacement) in implicitRegexes) {
            s = regex.replace(s, replacement)
        }

        val openCount = s.count { it == '(' }
        val closeCount = s.count { it == ')' }
        if (strict && openCount != closeCount) {
            throw IllegalArgumentException("Mismatched parentheses ($openCount open, $closeCount closed)")
        }

        if (!strict && openCount > closeCount) {
            s += ")".repeat(openCount - closeCount)
        }

        return s
    }

    private fun preprocessPercentages(input: String): String {
        var s = input
        val addSubPctRegex = Regex("((?:\\d+(?:\\.\\d+)?|\\([^)]+\\)))\\s*([+\\-])\\s*(\\d+(?:\\.\\d+)?)\\s*%")
        while (addSubPctRegex.containsMatchIn(s)) {
            s = addSubPctRegex.replace(s) { match ->
                val base = match.groupValues[1]
                val op = match.groupValues[2]
                val pct = match.groupValues[3]
                "$base $op ($base * $pct / 100)"
            }
        }

        val mulDivPctRegex = Regex("((?:\\d+(?:\\.\\d+)?|\\([^)]+\\)))\\s*([*/])\\s*(\\d+(?:\\.\\d+)?)\\s*%")
        while (mulDivPctRegex.containsMatchIn(s)) {
            s = mulDivPctRegex.replace(s) { match ->
                val base = match.groupValues[1]
                val op = match.groupValues[2]
                val pct = match.groupValues[3]
                "$base $op ($pct / 100)"
            }
        }

        val standalonePctRegex = Regex("(\\d+(?:\\.\\d+)?)\\s*%")
        s = standalonePctRegex.replace(s) { match ->
            "(${match.groupValues[1]} / 100)"
        }

        return s
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        val len = expr.length

        while (i < len) {
            val c = expr[i]
            when {
                c.isWhitespace() -> i++
                c in "+-*/^()%!" -> {
                    // Check for unary minus vs binary minus
                    if (c == '-') {
                        val prevToken = tokens.lastOrNull()
                        val isUnary = prevToken == null || prevToken in "+-*/^(," ||
                            prevToken in setOf("sin", "cos", "tan", "asin", "acos", "atan", "log", "ln", "sqrt", "abs", "neg")
                        if (isUnary) {
                            tokens.add("neg")
                            i++
                            continue
                        }
                    }
                    tokens.add(c.toString())
                    i++
                }
                c.isDigit() || (c == '.' && i + 1 < len && expr[i + 1].isDigit()) -> {
                    val sb = StringBuilder()
                    while (i < len && (expr[i].isDigit() || expr[i] == '.')) {
                        sb.append(expr[i])
                        i++
                    }
                    // Check for scientific notation exponent: e.g. 1e3, 1E-5, 2.5e+4
                    if (i < len && (expr[i] == 'e' || expr[i] == 'E')) {
                        val nextIdx = i + 1
                        if (nextIdx < len) {
                            val nextChar = expr[nextIdx]
                            if (nextChar.isDigit() || ((nextChar == '+' || nextChar == '-') && nextIdx + 1 < len && expr[nextIdx + 1].isDigit())) {
                                sb.append(expr[i]) // append 'e' / 'E'
                                i++
                                if (expr[i] == '+' || expr[i] == '-') {
                                    sb.append(expr[i])
                                    i++
                                }
                                while (i < len && expr[i].isDigit()) {
                                    sb.append(expr[i])
                                    i++
                                }
                            }
                        }
                    }
                    tokens.add(sb.toString())
                }
                c.isLetter() -> {
                    val sb = StringBuilder()
                    while (i < len && expr[i].isLetter()) {
                        sb.append(expr[i])
                        i++
                    }
                    val word = sb.toString().lowercase()
                    tokens.add(word)
                }
                else -> {
                    throw IllegalArgumentException("Unexpected character '$c' at position $i")
                }
            }
        }
        return tokens
    }

    private fun precedence(op: String): Int {
        return when (op) {
            "+", "-" -> 1
            "*", "/", "%" -> 2
            "neg" -> 3   // Unary minus binds less tightly than power (-2^2 = -(2^2) = -4)
            "^" -> 4     // Power has higher precedence than unary minus
            "!" -> 5
            "sin", "cos", "tan", "asin", "acos", "atan", "sinh", "cosh", "tanh", "asinh", "acosh", "atanh", "log", "ln", "sqrt", "abs" -> 6
            else -> 0
        }
    }

    private fun isRightAssociative(op: String): Boolean {
        return op == "^" || op == "neg"
    }

    private fun shuntingYard(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val operatorStack = mutableListOf<String>()

        val functions = setOf("sin", "cos", "tan", "asin", "acos", "atan", "sinh", "cosh", "tanh", "asinh", "acosh", "atanh", "log", "ln", "sqrt", "abs")

        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null -> output.add(token)
                token == "pi" || token == "e" -> output.add(token)
                token in functions -> operatorStack.add(token)
                token in listOf("+", "-", "*", "/", "%", "^", "neg", "!") -> {
                    while (operatorStack.isNotEmpty()) {
                        val top = operatorStack.last()
                        if (top == "(") break
                        // Prefix unary operator "neg" should not pop a preceding operator from the stack
                        if (token == "neg") break

                        val p1 = precedence(token)
                        val p2 = precedence(top)
                        if (p2 > p1 || (p2 == p1 && !isRightAssociative(token))) {
                            output.add(operatorStack.removeAt(operatorStack.lastIndex))
                        } else {
                            break
                        }
                    }
                    operatorStack.add(token)
                }
                token == "(" -> operatorStack.add(token)
                token == ")" -> {
                    var matched = false
                    while (operatorStack.isNotEmpty()) {
                        val top = operatorStack.removeAt(operatorStack.lastIndex)
                        if (top == "(") {
                            matched = true
                            break
                        }
                        output.add(top)
                    }
                    if (!matched) throw IllegalArgumentException("Mismatched parentheses")
                    if (operatorStack.isNotEmpty() && operatorStack.last() in functions) {
                        output.add(operatorStack.removeAt(operatorStack.lastIndex))
                    }
                }
                else -> throw IllegalArgumentException("Unknown identifier or token: $token")
            }
        }

        while (operatorStack.isNotEmpty()) {
            val top = operatorStack.removeAt(operatorStack.lastIndex)
            if (top == "(" || top == ")") throw IllegalArgumentException("Mismatched parentheses")
            output.add(top)
        }

        return output
    }

    private fun evalRPN(rpn: List<String>, isDegreeMode: Boolean): Double {
        val stack = mutableListOf<Double>()

        for (token in rpn) {
            val num = token.toDoubleOrNull()
            if (num != null) {
                stack.add(num)
                continue
            }

            when (token) {
                "pi" -> stack.add(PI)
                "e" -> stack.add(E)
                "neg" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Syntax Error: missing operand for unary minus")
                    val a = stack.removeAt(stack.lastIndex)
                    stack.add(-a)
                }
                "!" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Syntax Error: missing operand for factorial")
                    val a = stack.removeAt(stack.lastIndex)
                    stack.add(factorial(a))
                }
                "+", "-", "*", "/", "%", "^" -> {
                    if (stack.size < 2) throw IllegalArgumentException("Syntax Error: missing operand for operator $token")
                    val b = stack.removeAt(stack.lastIndex)
                    val a = stack.removeAt(stack.lastIndex)
                    val res = when (token) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> if (b == 0.0) Double.NaN else a / b
                        "%" -> a % b
                        "^" -> a.pow(b)
                        else -> 0.0
                    }
                    stack.add(res)
                }
                "sin", "cos", "tan", "asin", "acos", "atan", "sinh", "cosh", "tanh", "asinh", "acosh", "atanh", "log", "ln", "sqrt", "abs" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Syntax Error: missing argument for function $token")
                    val a = stack.removeAt(stack.lastIndex)
                    val radVal = if (isDegreeMode) Math.toRadians(a) else a
                    val res = when (token) {
                        "sin" -> sin(radVal)
                        "cos" -> cos(radVal)
                        "tan" -> {
                            if (isDegreeMode) {
                                val norm = abs(a) % 180.0
                                if (abs(norm - 90.0) < 1e-9) {
                                    Double.NaN // Undefined at 90 deg + k * 180 deg
                                } else {
                                    tan(radVal)
                                }
                            } else {
                                val norm = abs(a - PI / 2) % PI
                                if (norm < 1e-12 || abs(norm - PI) < 1e-12) {
                                    Double.NaN
                                } else {
                                    tan(radVal)
                                }
                            }
                        }
                        "asin" -> {
                            val v = asin(a)
                            if (isDegreeMode) Math.toDegrees(v) else v
                        }
                        "acos" -> {
                            val v = acos(a)
                            if (isDegreeMode) Math.toDegrees(v) else v
                        }
                        "atan" -> {
                            val v = atan(a)
                            if (isDegreeMode) Math.toDegrees(v) else v
                        }
                        "sinh" -> sinh(a)
                        "cosh" -> cosh(a)
                        "tanh" -> tanh(a)
                        "asinh" -> asinh(a)
                        "acosh" -> acosh(a)
                        "atanh" -> atanh(a)
                        "log" -> if (a <= 0.0) Double.NaN else log10(a)
                        "ln" -> if (a <= 0.0) Double.NaN else ln(a)
                        "sqrt" -> if (a < 0.0) Double.NaN else sqrt(a)
                        "abs" -> abs(a)
                        else -> 0.0
                    }
                    stack.add(res)
                }
                else -> throw IllegalArgumentException("Unknown operator: $token")
            }
        }

        if (stack.size != 1) throw IllegalArgumentException("Invalid Expression")
        return stack.first()
    }

    private fun factorial(n: Double): Double {
        if (n < 0 || n != floor(n)) throw IllegalArgumentException("Factorial undefined for non-integers")
        if (n > 170) return Double.POSITIVE_INFINITY
        var res = 1.0
        for (i in 2..n.toInt()) {
            res *= i
        }
        return res
    }

    fun formatNumber(value: Double, precision: Int = -1, formatStyle: String = "STANDARD"): String {
        if (value.isNaN()) return "Error"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"

        // Round tiny precision artifacts e.g. 0.0000000000000001 -> 0
        val rounded = if (abs(value) < 1e-12) 0.0 else value

        if (formatStyle.equals("SCIENTIFIC", ignoreCase = true)) {
            return "%.6e".format(Locale.US, rounded)
        }

        val absVal = abs(rounded)
        // Force scientific notation ONLY for extremely large or non-zero tiny numbers
        val useScientific = (absVal >= 1e12 || (absVal < 1e-6 && absVal > 0.0))
        if (useScientific) {
            return "%.6e".format(Locale.US, rounded)
        }

        val baseStr: String = if (precision >= 0) {
            "%.${precision}f".format(Locale.US, rounded)
        } else if (rounded == floor(rounded) && absVal < 1e12) {
            rounded.toLong().toString()
        } else {
            val str = "%.8f".format(Locale.US, rounded)
            str.dropLastWhile { it == '0' }.dropLastWhile { it == '.' }
        }

        if (baseStr.contains("e", ignoreCase = true) || baseStr == "Error" || baseStr.contains("Infinity")) {
            return baseStr
        }

        val parts = baseStr.split(".")
        val integerPart = parts[0]
        val decimalPart = if (parts.size > 1) parts[1] else null

        val isNegative = integerPart.startsWith("-")
        val absInt = if (isNegative) integerPart.substring(1) else integerPart

        val formattedInt = when (formatStyle.uppercase(Locale.US)) {
            "EUROPEAN" -> {
                val grouped = formatThousands(absInt, ".")
                if (isNegative) "-$grouped" else grouped
            }
            "INDIAN" -> {
                val grouped = formatIndianGrouping(absInt, ",")
                if (isNegative) "-$grouped" else grouped
            }
            "PLAIN" -> {
                integerPart
            }
            else -> { // STANDARD
                val grouped = formatThousands(absInt, ",")
                if (isNegative) "-$grouped" else grouped
            }
        }

        return if (decimalPart != null) {
            val decimalSep = if (formatStyle.equals("EUROPEAN", ignoreCase = true)) "," else "."
            "$formattedInt$decimalSep$decimalPart"
        } else {
            formattedInt
        }
    }

    fun formatExpressionDisplay(expr: String, formatStyle: String = "STANDARD"): String {
        if (expr.isBlank()) return expr
        if (formatStyle.equals("PLAIN", ignoreCase = true)) return expr

        val cleanExpr = expr.replace("*", "×").replace("/", "÷").replace("-", "−")
        val numRegex = Regex("\\d+(\\.\\d+)?([eE][+-]?\\d+)?")
        return numRegex.replace(cleanExpr) { match ->
            val numStr = match.value
            // If it contains scientific notation, leave exponent alone and format mantissa
            if (numStr.contains("e", ignoreCase = true)) {
                val parts = numStr.split(Regex("[eE]"))
                val mantissa = parts[0]
                val exp = if (parts.size > 1) parts[1] else ""
                val formattedMantissa = formatNumberParts(mantissa, formatStyle)
                "$formattedMantissa" + "e" + exp
            } else {
                formatNumberParts(numStr, formatStyle)
            }
        }
    }

    private fun formatNumberParts(numStr: String, formatStyle: String): String {
        val parts = numStr.split(".")
        val intPart = parts[0]
        val decPart = if (parts.size > 1) parts[1] else null

        val formattedInt = when (formatStyle.uppercase(Locale.US)) {
            "EUROPEAN" -> formatThousands(intPart, ".")
            "INDIAN" -> formatIndianGrouping(intPart, ",")
            else -> formatThousands(intPart, ",")
        }

        return if (decPart != null) {
            val decSep = if (formatStyle.equals("EUROPEAN", ignoreCase = true)) "," else "."
            "$formattedInt$decSep$decPart"
        } else {
            formattedInt
        }
    }

    private fun formatThousands(str: String, separator: String): String {
        if (str.length <= 3) return str
        val sb = StringBuilder()
        val len = str.length
        for (i in 0 until len) {
            if (i > 0 && (len - i) % 3 == 0) {
                sb.append(separator)
            }
            sb.append(str[i])
        }
        return sb.toString()
    }

    private fun formatIndianGrouping(str: String, separator: String): String {
        if (str.length <= 3) return str
        val lastThree = str.substring(str.length - 3)
        var remaining = str.substring(0, str.length - 3)
        val sb = StringBuilder()
        while (remaining.length > 2) {
            val chunk = remaining.substring(remaining.length - 2)
            sb.insert(0, "$separator$chunk")
            remaining = remaining.substring(0, remaining.length - 2)
        }
        if (remaining.isNotEmpty()) {
            sb.insert(0, remaining)
        }
        return "$sb$separator$lastThree"
    }
}

sealed class EvaluationResult {
    data class Success(val rawValue: Double, val formattedResult: String) : EvaluationResult()
    data class Error(val message: String) : EvaluationResult()
}

