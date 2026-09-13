package com.example

import com.example.data.currency.CurrencyRepository
import com.example.data.currency.defaultRates
import com.example.util.EvaluationResult
import com.example.util.MathEvaluator
import com.example.util.MatrixData
import com.example.util.MatrixResult
import com.example.util.MatrixUtils
import com.example.util.UnitCategory
import com.example.util.UnitConverter
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
        val res = MathEvaluator.evaluateStrict("tan(90)", isDegree = true)
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
        assertEquals(0.6, inv.data[0][0], 1e-9)
        assertEquals(-0.7, inv.data[0][1], 1e-9)
        assertEquals(-0.2, inv.data[1][0], 1e-9)
        assertEquals(0.4, inv.data[1][1], 1e-9)
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
}

