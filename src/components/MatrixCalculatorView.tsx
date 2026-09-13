import React, { useState } from 'react';
import { MatrixDimensions, MatrixOperation } from '../types';
import { MatrixUtils } from '../utils/matrixUtils';

export const MatrixCalculatorView: React.FC = () => {
  const [dimension, setDimension] = useState<MatrixDimensions>('2x2');
  const size = dimension === '2x2' ? 2 : 3;

  const createEmptyMatrix = (n: number) =>
    Array.from({ length: n }, () => Array(n).fill(0));

  const [matrixA, setMatrixA] = useState<number[][]>(createEmptyMatrix(2));
  const [matrixB, setMatrixB] = useState<number[][]>(createEmptyMatrix(2));
  const [scalarResult, setScalarResult] = useState<number | null>(null);
  const [resultMatrix, setResultMatrix] = useState<number[][] | null>(null);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const handleDimensionChange = (dim: MatrixDimensions) => {
    setDimension(dim);
    const n = dim === '2x2' ? 2 : 3;
    setMatrixA(createEmptyMatrix(n));
    setMatrixB(createEmptyMatrix(n));
    setResultMatrix(null);
    setScalarResult(null);
    setErrorMsg(null);
  };

  const handleCellChange = (
    isA: boolean,
    row: number,
    col: number,
    valStr: string
  ) => {
    const val = parseFloat(valStr) || 0;
    if (isA) {
      setMatrixA((prev) => {
        const copy = prev.map((r) => [...r]);
        copy[row][col] = val;
        return copy;
      });
    } else {
      setMatrixB((prev) => {
        const copy = prev.map((r) => [...r]);
        copy[row][col] = val;
        return copy;
      });
    }
  };

  const performOperation = (op: MatrixOperation) => {
    setErrorMsg(null);
    setScalarResult(null);
    setResultMatrix(null);

    try {
      switch (op) {
        case 'add':
          setResultMatrix(MatrixUtils.add(matrixA, matrixB));
          break;
        case 'subtract':
          setResultMatrix(MatrixUtils.subtract(matrixA, matrixB));
          break;
        case 'multiply':
          setResultMatrix(MatrixUtils.multiply(matrixA, matrixB));
          break;
        case 'detA':
          setScalarResult(MatrixUtils.determinant(matrixA));
          break;
        case 'detB':
          setScalarResult(MatrixUtils.determinant(matrixB));
          break;
        case 'transposeA':
          setResultMatrix(MatrixUtils.transpose(matrixA));
          break;
        case 'transposeB':
          setResultMatrix(MatrixUtils.transpose(matrixB));
          break;
        case 'inverseA': {
          const inv = MatrixUtils.inverse(matrixA);
          if (!inv) setErrorMsg('Matrix A is singular (Determinant is 0), inverse does not exist.');
          else setResultMatrix(inv);
          break;
        }
        case 'inverseB': {
          const inv = MatrixUtils.inverse(matrixB);
          if (!inv) setErrorMsg('Matrix B is singular (Determinant is 0), inverse does not exist.');
          else setResultMatrix(inv);
          break;
        }
      }
    } catch (e: any) {
      setErrorMsg(e.message || 'Error evaluating matrix');
    }
  };

  return (
    <div className="flex flex-col h-full max-w-lg mx-auto w-full p-4 overflow-y-auto">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-xl font-bold text-white">Matrix Calculator</h2>
        <div className="flex items-center space-x-1.5 bg-slate-900 p-1 rounded-xl border border-slate-800">
          <button
            onClick={() => handleDimensionChange('2x2')}
            className={`px-3 py-1 rounded-lg text-xs font-semibold transition-all ${
              dimension === '2x2'
                ? 'bg-sky-500 text-white shadow-sm'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            2 × 2
          </button>
          <button
            onClick={() => handleDimensionChange('3x3')}
            className={`px-3 py-1 rounded-lg text-xs font-semibold transition-all ${
              dimension === '3x3'
                ? 'bg-sky-500 text-white shadow-sm'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            3 × 3
          </button>
        </div>
      </div>

      {/* Matrices input grids */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-4">
        {/* Matrix A */}
        <div className="p-3 rounded-2xl bg-slate-900 border border-slate-800">
          <div className="text-xs font-bold text-sky-400 uppercase tracking-wider mb-2">
            Matrix A
          </div>
          <div
            className="grid gap-2"
            style={{ gridTemplateColumns: `repeat(${size}, minmax(0, 1fr))` }}
          >
            {matrixA.map((row, rIdx) =>
              row.map((val, cIdx) => (
                <input
                  key={`a-${rIdx}-${cIdx}`}
                  type="number"
                  value={val === 0 ? '' : val}
                  onChange={(e) => handleCellChange(true, rIdx, cIdx, e.target.value)}
                  placeholder="0"
                  className="w-full h-11 text-center bg-slate-800 border border-slate-700/80 rounded-xl text-white font-mono text-sm font-semibold outline-none focus:border-sky-500"
                />
              ))
            )}
          </div>
        </div>

        {/* Matrix B */}
        <div className="p-3 rounded-2xl bg-slate-900 border border-slate-800">
          <div className="text-xs font-bold text-indigo-400 uppercase tracking-wider mb-2">
            Matrix B
          </div>
          <div
            className="grid gap-2"
            style={{ gridTemplateColumns: `repeat(${size}, minmax(0, 1fr))` }}
          >
            {matrixB.map((row, rIdx) =>
              row.map((val, cIdx) => (
                <input
                  key={`b-${rIdx}-${cIdx}`}
                  type="number"
                  value={val === 0 ? '' : val}
                  onChange={(e) => handleCellChange(false, rIdx, cIdx, e.target.value)}
                  placeholder="0"
                  className="w-full h-11 text-center bg-slate-800 border border-slate-700/80 rounded-xl text-white font-mono text-sm font-semibold outline-none focus:border-indigo-500"
                />
              ))
            )}
          </div>
        </div>
      </div>

      {/* Operations Toolbar */}
      <div className="grid grid-cols-3 sm:grid-cols-5 gap-1.5 mb-4">
        <button
          onClick={() => performOperation('add')}
          className="py-2 rounded-xl bg-slate-800 text-sky-300 border border-slate-700 font-semibold text-xs hover:bg-slate-700 active:scale-95 transition-all"
        >
          A + B
        </button>
        <button
          onClick={() => performOperation('subtract')}
          className="py-2 rounded-xl bg-slate-800 text-sky-300 border border-slate-700 font-semibold text-xs hover:bg-slate-700 active:scale-95 transition-all"
        >
          A − B
        </button>
        <button
          onClick={() => performOperation('multiply')}
          className="py-2 rounded-xl bg-slate-800 text-sky-300 border border-slate-700 font-semibold text-xs hover:bg-slate-700 active:scale-95 transition-all"
        >
          A × B
        </button>
        <button
          onClick={() => performOperation('detA')}
          className="py-2 rounded-xl bg-slate-800 text-slate-300 border border-slate-700 font-semibold text-xs hover:bg-slate-700 active:scale-95 transition-all"
        >
          det(A)
        </button>
        <button
          onClick={() => performOperation('detB')}
          className="py-2 rounded-xl bg-slate-800 text-slate-300 border border-slate-700 font-semibold text-xs hover:bg-slate-700 active:scale-95 transition-all"
        >
          det(B)
        </button>
        <button
          onClick={() => performOperation('transposeA')}
          className="py-2 rounded-xl bg-slate-800 text-slate-300 border border-slate-700 font-semibold text-xs hover:bg-slate-700 active:scale-95 transition-all"
        >
          Aᵀ
        </button>
        <button
          onClick={() => performOperation('transposeB')}
          className="py-2 rounded-xl bg-slate-800 text-slate-300 border border-slate-700 font-semibold text-xs hover:bg-slate-700 active:scale-95 transition-all"
        >
          Bᵀ
        </button>
        <button
          onClick={() => performOperation('inverseA')}
          className="py-2 rounded-xl bg-slate-800 text-emerald-300 border border-slate-700 font-semibold text-xs hover:bg-slate-700 active:scale-95 transition-all"
        >
          A⁻¹
        </button>
        <button
          onClick={() => performOperation('inverseB')}
          className="py-2 rounded-xl bg-slate-800 text-emerald-300 border border-slate-700 font-semibold text-xs hover:bg-slate-700 active:scale-95 transition-all"
        >
          B⁻¹
        </button>
        <button
          onClick={() => {
            setMatrixA(createEmptyMatrix(size));
            setMatrixB(createEmptyMatrix(size));
            setResultMatrix(null);
            setScalarResult(null);
            setErrorMsg(null);
          }}
          className="py-2 rounded-xl bg-rose-500/20 text-rose-300 border border-rose-500/30 font-semibold text-xs hover:bg-rose-500/30 active:scale-95 transition-all"
        >
          Clear
        </button>
      </div>

      {/* Result Display */}
      {errorMsg && (
        <div className="p-3 mb-4 rounded-xl bg-rose-500/15 border border-rose-500/25 text-rose-300 text-xs font-medium">
          {errorMsg}
        </div>
      )}

      {scalarResult !== null && (
        <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 flex items-center justify-between">
          <span className="text-sm font-medium text-slate-400">Determinant:</span>
          <span className="font-mono text-2xl font-bold text-sky-400">
            {parseFloat(scalarResult.toPrecision(8))}
          </span>
        </div>
      )}

      {resultMatrix && (
        <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800">
          <div className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">
            Result Matrix
          </div>
          <div
            className="grid gap-2"
            style={{
              gridTemplateColumns: `repeat(${resultMatrix[0].length}, minmax(0, 1fr))`,
            }}
          >
            {resultMatrix.map((row, rIdx) =>
              row.map((val, cIdx) => (
                <div
                  key={`res-${rIdx}-${cIdx}`}
                  className="h-11 flex items-center justify-center bg-slate-800/80 rounded-xl text-sky-300 font-mono text-sm font-bold border border-slate-700"
                >
                  {parseFloat(val.toFixed(4))}
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
};
