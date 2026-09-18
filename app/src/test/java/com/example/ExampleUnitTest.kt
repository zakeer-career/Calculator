package com.zakeercareer.calculator

import com.zakeercareer.calculator.data.currency.CurrencyRepository
import com.zakeercareer.calculator.data.currency.defaultRates
import com.zakeercareer.calculator.util.EvaluationResult
import com.zakeercareer.calculator.util.MathEvaluator
import com.zakeercareer.calculator.util.MatrixData
import com.zakeercareer.calculator.util.MatrixResult
import com.zakeercareer.calculator.util.MatrixUtils
import com.zakeercareer.calculator.util.UnitCategory
import com.zakeercareer.calculator.util.UnitConverter
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

class ExampleUnitTest {

    @Test
    fun testBasicMathAndPrecedence() {
        val result = MathEvaluator.evaluateStrict("2 + 3 * 4")
        assertTrue(result is EvaluationResult.Success)
        assertEquals(14.0, (result as EvaluationResult.Success).rawValue, 1e-9)
    }

    @Test
    fun testUnaryMinusAndPowerPrecedence() {
        // In mathematical convention and standard scientific calculators, -2^2 is -(2^2) = -4
        val res1 = MathEvaluator.evaluateStrict("-2^2")
        assertTrue(res1 is EvaluationResult.Success)
        assertEquals(-4.0, (res1 as EvaluationResult.Success).rawValue, 1e-9)

        // (-2)^2 is 4
        val res2 = MathEvaluator.evaluateStrict("(-2)^2")
        assertTrue(res2 is EvaluationResult.Success)
        assertEquals(4.0, (res2 as EvaluationResult.Success).rawValue, 1e-9)
    }

    @Test
    fun testScientificNotation() {
        val res = MathEvaluator.evaluateStrict("1.5e3 + 2.5e2")
        assertTrue(res is EvaluationResult.Success)
        assertEquals(1750.0, (res as EvaluationResult.Success).rawValue, 1e-9)
    }

    @Test
    fun testStrictEvaluationRejectsIncomplete() {
        // Strict evaluation rejects incomplete expressions
        val resTrailingOp = MathEvaluator.evaluateStrict("5 +")
        assertTrue(resTrailingOp is EvaluationResult.Error)

        val resUnmatchedParen = MathEvaluator.evaluateStrict("(5 + 3")
        assertTrue(resUnmatchedParen is EvaluationResult.Error)

        // Partial evaluation handles incomplete expressions gracefully
        val partial = MathEvaluator.evaluatePartial("5 +")
        assertTrue(partial is EvaluationResult.Success)
        assertEquals(5.0, (partial as EvaluationResult.Success).rawValue, 1e-9)
    }

    @Test
    fun testTrigSingularity() {
        // tan(90 deg) is undefined
        val res = MathEvaluator.evaluateStrict("tan(90)", isDegreeMode = true)
        assertTrue(res is EvaluationResult.Error)
    }

    @Test
    fun testMatrixDeterminantAndInverse() {
        val matA = MatrixData(
            2, 2,
            arrayOf(
                doubleArrayOf(4.0, 7.0),
                doubleArrayOf(2.0, 6.0)
            )
        )
        val detRes = MatrixUtils.determinant(matA)
        assertTrue(detRes is MatrixResult.SuccessScalar)
        assertEquals(10.0, (detRes as MatrixResult.SuccessScalar).scalar, 1e-9)

        val invRes = MatrixUtils.inverse(matA)
        assertTrue(invRes is MatrixResult.SuccessMatrix)
        val inv = (invRes as MatrixResult.SuccessMatrix).matrix
        assertEquals(0.6, inv.values[0][0], 1e-9)
        assertEquals(-0.7, inv.values[0][1], 1e-9)
        assertEquals(-0.2, inv.values[1][0], 1e-9)
        assertEquals(0.4, inv.values[1][1], 1e-9)
    }

    @Test
    fun testCurrencyConversionBigDecimal() {
        val converted = CurrencyRepository.convertCurrencyBigDecimal(
            BigDecimal("100.00"),
            "USD",
            "EUR",
            defaultRates
        )
        assertEquals(BigDecimal("92.0000"), converted)

        // Zero handling
        val zeroConverted = CurrencyRepository.convertCurrencyBigDecimal(
            BigDecimal.ZERO,
            "USD",
            "EUR",
            defaultRates
        )
        assertEquals(BigDecimal("0.0000"), zeroConverted)
    }

    @Test
    fun testUnitConverterLength() {
        val units = UnitConverter.getUnits(UnitCategory.LENGTH)
        val km = units.first { it.symbol == "km" }
        val m = units.first { it.symbol == "m" }
        val res = UnitConverter.convert(2.5, UnitCategory.LENGTH, km, m)
        assertEquals(2500.0, res, 1e-9)
    }

    @Test
    fun testCurrencyConversionsVariousPairs() {
        // EUR to GBP via USD base
        // 100 EUR in USD = 100 / 0.92 = 108.695652...
        // in GBP = (100 / 0.92) * 0.79 = 85.869565...
        val eurToGbp = CurrencyRepository.convertCurrencyBigDecimal(
            BigDecimal("100.00"),
            "EUR",
            "GBP",
            defaultRates
        )
        assertTrue(eurToGbp > BigDecimal("85.8") && eurToGbp < BigDecimal("85.9"))

        // Same currency conversion
        val sameCurr = CurrencyRepository.convertCurrencyBigDecimal(
            BigDecimal("50.00"),
            "JPY",
            "JPY",
            defaultRates
        )
        assertEquals(BigDecimal("50.0000"), sameCurr)
    }

    @Test
    fun testMathEvaluatorTrigAndLogs() {
        // sin(30 deg) = 0.5
        val sin30 = MathEvaluator.evaluateStrict("sin(30)", isDegreeMode = true)
        assertTrue(sin30 is EvaluationResult.Success)
        assertEquals(0.5, (sin30 as EvaluationResult.Success).rawValue, 1e-9)

        // cos(60 deg) = 0.5
        val cos60 = MathEvaluator.evaluateStrict("cos(60)", isDegreeMode = true)
        assertTrue(cos60 is EvaluationResult.Success)
        assertEquals(0.5, (cos60 as EvaluationResult.Success).rawValue, 1e-9)

        // log10(1000) = 3
        val log1000 = MathEvaluator.evaluateStrict("log(1000)")
        assertTrue(log1000 is EvaluationResult.Success)
        assertEquals(3.0, (log1000 as EvaluationResult.Success).rawValue, 1e-9)

        // ln(e) = 1
        val lne = MathEvaluator.evaluateStrict("ln(e)")
        assertTrue(lne is EvaluationResult.Success)
        assertEquals(1.0, (lne as EvaluationResult.Success).rawValue, 1e-9)
    }

    @Test
    fun testMatrixAdditionAndMultiplication() {
        val matA = MatrixData(
            2, 2,
            arrayOf(
                doubleArrayOf(1.0, 2.0),
                doubleArrayOf(3.0, 4.0)
            )
        )
        val matB = MatrixData(
            2, 2,
            arrayOf(
                doubleArrayOf(5.0, 6.0),
                doubleArrayOf(7.0, 8.0)
            )
        )

        // Addition: [ [6, 8], [10, 12] ]
        val addRes = MatrixUtils.add(matA, matB)
        assertTrue(addRes is MatrixResult.SuccessMatrix)
        val addMat = (addRes as MatrixResult.SuccessMatrix).matrix
        assertEquals(6.0, addMat.values[0][0], 1e-9)
        assertEquals(8.0, addMat.values[0][1], 1e-9)
        assertEquals(10.0, addMat.values[1][0], 1e-9)
        assertEquals(12.0, addMat.values[1][1], 1e-9)

        // Multiplication: [ [1*5+2*7, 1*6+2*8], [3*5+4*7, 3*6+4*8] ] = [ [19, 22], [43, 50] ]
        val mulRes = MatrixUtils.multiply(matA, matB)
        assertTrue(mulRes is MatrixResult.SuccessMatrix)
        val mulMat = (mulRes as MatrixResult.SuccessMatrix).matrix
        assertEquals(19.0, mulMat.values[0][0], 1e-9)
        assertEquals(22.0, mulMat.values[0][1], 1e-9)
        assertEquals(43.0, mulMat.values[1][0], 1e-9)
        assertEquals(50.0, mulMat.values[1][1], 1e-9)
    }

    @Test
    fun testDivisionByZeroHandledGracefully() {
        val res = MathEvaluator.evaluateStrict("10 / 0")
        assertTrue(res is EvaluationResult.Error)
        val errMsg = (res as EvaluationResult.Error).message
        assertTrue(errMsg.contains("zero", ignoreCase = true) || errMsg.contains("cannot divide", ignoreCase = true))
    }

    @Test
    fun testFactorialPrecisionAndPostfixValidation() {
        // Floating point precision tolerance: (0.1 + 0.2) * 10 is 3.0000000000000004
        val resPrecision = MathEvaluator.evaluateStrict("((0.1 + 0.2) * 10)!")
        assertTrue(resPrecision is EvaluationResult.Success)
        assertEquals(6.0, (resPrecision as EvaluationResult.Success).rawValue, 1e-9)

        // Postfix 5! = 120
        val res5Fact = MathEvaluator.evaluateStrict("5!")
        assertTrue(res5Fact is EvaluationResult.Success)
        assertEquals(120.0, (res5Fact as EvaluationResult.Success).rawValue, 1e-9)

        // Misplaced prefix !5 should fail
        val resPrefix = MathEvaluator.evaluateStrict("!5")
        assertTrue(resPrefix is EvaluationResult.Error)
    }

    @Test
    fun testImplicitMultiplicationWithEulerE() {
        val res = MathEvaluator.evaluateStrict("(2 + 3)e")
        assertTrue(res is EvaluationResult.Success)
        assertEquals(5.0 * Math.E, (res as EvaluationResult.Success).rawValue, 1e-9)

        val resE5 = MathEvaluator.evaluateStrict("e(5)")
        assertTrue(resE5 is EvaluationResult.Success)
        assertEquals(5.0 * Math.E, (resE5 as EvaluationResult.Success).rawValue, 1e-9)
    }

    @Test
    fun testScientificNotationWithPercentage() {
        // 1e3% = 1000 / 100 = 10
        val resPct = MathEvaluator.evaluateStrict("1e3%")
        assertTrue(resPct is EvaluationResult.Success)
        assertEquals(10.0, (resPct as EvaluationResult.Success).rawValue, 1e-9)

        // 100 + 1e2% = 100 + (100 * 100 / 100) = 200
        val resAddPct = MathEvaluator.evaluateStrict("100 + 1e2%")
        assertTrue(resAddPct is EvaluationResult.Success)
        assertEquals(200.0, (resAddPct as EvaluationResult.Success).rawValue, 1e-9)
    }

    @Test
    fun testMathematicalDomainErrors() {
        // asin(2) domain error: -1 <= x <= 1
        val resAsin = MathEvaluator.evaluateStrict("asin(2)")
        assertTrue(resAsin is EvaluationResult.Error)

        // sqrt(-4) domain error: x >= 0
        val resSqrt = MathEvaluator.evaluateStrict("sqrt(-4)")
        assertTrue(resSqrt is EvaluationResult.Error)

        // log(-10) domain error: x > 0
        val resLog = MathEvaluator.evaluateStrict("log(-10)")
        assertTrue(resLog is EvaluationResult.Error)

        // tan(90) in degree mode
        val resTan90 = MathEvaluator.evaluateStrict("tan(90)", isDegreeMode = true)
        assertTrue(resTan90 is EvaluationResult.Error)
    }

    @Test
    fun testRateSourceEnum() {
        val state = com.zakeercareer.calculator.data.currency.ExchangeRatesState()
        assertEquals(com.zakeercareer.calculator.data.currency.RateSource.DEFAULT, state.rateSource)
        assertFalse(state.isRealtime)
    }
}

