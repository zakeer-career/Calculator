package com.zakeercareer.calculator.util

import kotlin.math.abs

data class MatrixData(
    val rows: Int,
    val cols: Int,
    val values: Array<DoubleArray>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MatrixData) return false
        if (rows != other.rows || cols != other.cols) return false
        return values.contentDeepEquals(other.values)
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + cols
        result = 31 * result + values.contentDeepHashCode()
        return result
    }

    fun formatted(): String {
        return values.joinToString("\n") { row ->
            row.joinToString("  ") { MathEvaluator.formatNumber(it) }
        }
    }

    companion object {
        fun zeros(rows: Int, cols: Int): MatrixData {
            return MatrixData(rows, cols, Array(rows) { DoubleArray(cols) { 0.0 } })
        }

        fun identity(size: Int): MatrixData {
            val m = zeros(size, size)
            for (i in 0 until size) {
                m.values[i][i] = 1.0
            }
            return m
        }
    }
}

object MatrixUtils {

    fun add(a: MatrixData, b: MatrixData): MatrixResult {
        if (a.rows != b.rows || a.cols != b.cols) {
            return MatrixResult.Error("Matrices must have identical dimensions for addition.")
        }
        val result = MatrixData.zeros(a.rows, a.cols)
        for (r in 0 until a.rows) {
            for (c in 0 until a.cols) {
                result.values[r][c] = a.values[r][c] + b.values[r][c]
            }
        }
        return MatrixResult.SuccessMatrix(result)
    }

    fun subtract(a: MatrixData, b: MatrixData): MatrixResult {
        if (a.rows != b.rows || a.cols != b.cols) {
            return MatrixResult.Error("Matrices must have identical dimensions for subtraction.")
        }
        val result = MatrixData.zeros(a.rows, a.cols)
        for (r in 0 until a.rows) {
            for (c in 0 until a.cols) {
                result.values[r][c] = a.values[r][c] - b.values[r][c]
            }
        }
        return MatrixResult.SuccessMatrix(result)
    }

    fun multiply(a: MatrixData, b: MatrixData): MatrixResult {
        if (a.cols != b.rows) {
            return MatrixResult.Error("Columns of Matrix A (${a.cols}) must equal Rows of Matrix B (${b.rows}).")
        }
        val result = MatrixData.zeros(a.rows, b.cols)
        for (r in 0 until a.rows) {
            for (c in 0 until b.cols) {
                var sum = 0.0
                for (k in 0 until a.cols) {
                    sum += a.values[r][k] * b.values[k][c]
                }
                result.values[r][c] = sum
            }
        }
        return MatrixResult.SuccessMatrix(result)
    }

    fun scalarMultiply(a: MatrixData, scalar: Double): MatrixResult {
        val result = MatrixData.zeros(a.rows, a.cols)
        for (r in 0 until a.rows) {
            for (c in 0 until a.cols) {
                result.values[r][c] = a.values[r][c] * scalar
            }
        }
        return MatrixResult.SuccessMatrix(result)
    }

    fun transpose(a: MatrixData): MatrixResult {
        val result = MatrixData.zeros(a.cols, a.rows)
        for (r in 0 until a.rows) {
            for (c in 0 until a.cols) {
                result.values[c][r] = a.values[r][c]
            }
        }
        return MatrixResult.SuccessMatrix(result)
    }

    fun determinant(a: MatrixData): MatrixResult {
        if (a.rows != a.cols) {
            return MatrixResult.Error("Determinant requires a square matrix.")
        }
        val det = calcDeterminant(a.values, a.rows)
        return MatrixResult.SuccessScalar(det)
    }

    private fun calcDeterminant(mat: Array<DoubleArray>, n: Int): Double {
        if (n == 1) return mat[0][0]
        if (n == 2) return mat[0][0] * mat[1][1] - mat[0][1] * mat[1][0]

        // Numerically stable O(n^3) Gaussian elimination with partial pivoting
        val a = Array(n) { mat[it].clone() }
        var det = 1.0

        for (i in 0 until n) {
            var pivotRow = i
            for (k in i + 1 until n) {
                if (abs(a[k][i]) > abs(a[pivotRow][i])) {
                    pivotRow = k
                }
            }

            if (abs(a[pivotRow][i]) < 1e-12) {
                return 0.0
            }

            if (pivotRow != i) {
                val temp = a[i]
                a[i] = a[pivotRow]
                a[pivotRow] = temp
                det = -det
            }

            val pivot = a[i][i]
            det *= pivot

            for (j in i + 1 until n) {
                val factor = a[j][i] / pivot
                for (c in i + 1 until n) {
                    a[j][c] -= factor * a[i][c]
                }
            }
        }

        return if (abs(det) < 1e-12) 0.0 else det
    }

    fun inverse(a: MatrixData): MatrixResult {
        if (a.rows != a.cols) {
            return MatrixResult.Error("Inverse requires a square matrix.")
        }
        val n = a.rows
        val det = calcDeterminant(a.values, n)
        if (abs(det) < 1e-12) {
            return MatrixResult.Error("Matrix is Singular (Determinant = 0). Inverse does not exist.")
        }

        val augmented = Array(n) { DoubleArray(2 * n) }
        for (i in 0 until n) {
            for (j in 0 until n) {
                augmented[i][j] = a.values[i][j]
            }
            augmented[i][i + n] = 1.0
        }

        // Gauss-Jordan elimination
        for (i in 0 until n) {
            var maxRow = i
            for (k in i + 1 until n) {
                if (abs(augmented[k][i]) > abs(augmented[maxRow][i])) {
                    maxRow = k
                }
            }
            val temp = augmented[i]
            augmented[i] = augmented[maxRow]
            augmented[maxRow] = temp

            val pivot = augmented[i][i]
            if (abs(pivot) < 1e-12) return MatrixResult.Error("Matrix is Singular.")

            for (j in 0 until 2 * n) {
                augmented[i][j] /= pivot
            }

            for (k in 0 until n) {
                if (k != i) {
                    val factor = augmented[k][i]
                    for (j in 0 until 2 * n) {
                        augmented[k][j] -= factor * augmented[i][j]
                    }
                }
            }
        }

        val inv = MatrixData.zeros(n, n)
        for (i in 0 until n) {
            for (j in 0 until n) {
                inv.values[i][j] = augmented[i][j + n]
            }
        }
        return MatrixResult.SuccessMatrix(inv)
    }

    fun trace(a: MatrixData): MatrixResult {
        if (a.rows != a.cols) {
            return MatrixResult.Error("Trace requires a square matrix.")
        }
        var tr = 0.0
        for (i in 0 until a.rows) {
            tr += a.values[i][i]
        }
        return MatrixResult.SuccessScalar(tr)
    }

    fun rank(a: MatrixData): MatrixResult {
        val r = a.rows
        val c = a.cols
        val mat = Array(r) { DoubleArray(c) }
        for (i in 0 until r) {
            for (j in 0 until c) {
                mat[i][j] = a.values[i][j]
            }
        }

        var rank = 0
        val selectedRow = BooleanArray(r)
        for (i in 0 until c) {
            var j = 0
            while (j < r) {
                if (!selectedRow[j] && abs(mat[j][i]) > 1e-10) break
                j++
            }
            if (j != r) {
                rank++
                selectedRow[j] = true
                for (p in 0 until r) {
                    if (p != j && abs(mat[p][i]) > 1e-10) {
                        val factor = mat[p][i] / mat[j][i]
                        for (k in i until c) {
                            mat[p][k] -= factor * mat[j][k]
                        }
                    }
                }
            }
        }
        return MatrixResult.SuccessScalar(rank.toDouble())
    }
}

sealed class MatrixResult {
    data class SuccessMatrix(val matrix: MatrixData) : MatrixResult()
    data class SuccessScalar(val scalar: Double) : MatrixResult()
    data class Error(val message: String) : MatrixResult()
}
