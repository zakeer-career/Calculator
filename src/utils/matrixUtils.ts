export class MatrixUtils {
  static add(a: number[][], b: number[][]): number[][] {
    return a.map((row, i) => row.map((val, j) => val + b[i][j]));
  }

  static subtract(a: number[][], b: number[][]): number[][] {
    return a.map((row, i) => row.map((val, j) => val - b[i][j]));
  }

  static multiply(a: number[][], b: number[][]): number[][] {
    const rowsA = a.length;
    const colsA = a[0].length;
    const colsB = b[0].length;
    const result: number[][] = Array.from({ length: rowsA }, () => Array(colsB).fill(0));

    for (let i = 0; i < rowsA; i++) {
      for (let j = 0; j < colsB; j++) {
        let sum = 0;
        for (let k = 0; k < colsA; k++) {
          sum += a[i][k] * b[k][j];
        }
        result[i][j] = sum;
      }
    }
    return result;
  }

  static transpose(matrix: number[][]): number[][] {
    return matrix[0].map((_, colIndex) => matrix.map((row) => row[colIndex]));
  }

  static determinant(matrix: number[][]): number {
    const n = matrix.length;
    if (n === 1) return matrix[0][0];
    if (n === 2) {
      return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
    }
    if (n === 3) {
      return (
        matrix[0][0] * (matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1]) -
        matrix[0][1] * (matrix[1][0] * matrix[2][2] - matrix[1][2] * matrix[2][0]) +
        matrix[0][2] * (matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0])
      );
    }
    return 0;
  }

  static inverse(matrix: number[][]): number[][] | null {
    const det = this.determinant(matrix);
    if (Math.abs(det) < 1e-10) return null; // Non-invertible

    const n = matrix.length;
    if (n === 2) {
      return [
        [matrix[1][1] / det, -matrix[0][1] / det],
        [-matrix[1][0] / det, matrix[0][0] / det],
      ];
    }

    if (n === 3) {
      const adj: number[][] = Array.from({ length: 3 }, () => Array(3).fill(0));

      // Minors and cofactors
      for (let i = 0; i < 3; i++) {
        for (let j = 0; j < 3; j++) {
          const subMatrix: number[][] = [];
          for (let r = 0; r < 3; r++) {
            if (r === i) continue;
            const row: number[] = [];
            for (let c = 0; c < 3; c++) {
              if (c === j) continue;
              row.push(matrix[r][c]);
            }
            subMatrix.push(row);
          }
          const sign = (i + j) % 2 === 0 ? 1 : -1;
          // Transpose adjugate
          adj[j][i] = (sign * this.determinant(subMatrix)) / det;
        }
      }
      return adj;
    }

    return null;
  }
}
