import React, { useState } from 'react';
import { Delete, Equal } from 'lucide-react';

interface ScientificCalculatorProps {
  expression: string;
  previewResult: string;
  isDegreeMode: boolean;
  onToggleDegreeMode: () => void;
  onInputChar: (char: string) => void;
  onClear: () => void;
  onDelete: () => void;
  onEvaluate: () => void;
  onToggleSign: () => void;
}

export const ScientificCalculator: React.FC<ScientificCalculatorProps> = ({
  expression,
  previewResult,
  isDegreeMode,
  onToggleDegreeMode,
  onInputChar,
  onClear,
  onDelete,
  onEvaluate,
  onToggleSign,
}) => {
  const [isSecondMode, setIsSecondMode] = useState(false);

  return (
    <div className="flex flex-col h-full max-w-lg mx-auto w-full p-2 select-none">
      {/* Display Screen */}
      <div className="flex flex-col justify-end items-end p-4 mb-3 rounded-2xl bg-slate-900/90 border border-slate-800 shadow-inner min-h-[120px] max-h-[150px] overflow-hidden">
        <div className="w-full flex items-center justify-between text-xs font-mono text-slate-500 mb-1">
          <span className="px-1.5 py-0.5 rounded bg-slate-800 text-sky-400 font-semibold uppercase">
            {isDegreeMode ? 'DEG' : 'RAD'}
          </span>
          <span>{isSecondMode ? '2nd Active' : ''}</span>
        </div>
        <div className="w-full text-right font-mono text-slate-400 text-base md:text-lg tracking-wide overflow-x-auto whitespace-nowrap scrollbar-none">
          {expression || '0'}
        </div>
        <div className="w-full text-right font-mono text-2xl md:text-3xl font-bold tracking-tight text-white mt-1 overflow-x-auto whitespace-nowrap scrollbar-none">
          {previewResult ? `= ${previewResult}` : (expression ? '' : '0')}
        </div>
      </div>

      {/* Mode Toolbar */}
      <div className="flex items-center justify-between gap-1.5 mb-2">
        <button
          onClick={onToggleDegreeMode}
          className={`flex-1 py-1.5 rounded-xl text-xs font-semibold border transition-all ${
            isDegreeMode
              ? 'bg-sky-500/20 text-sky-300 border-sky-500/30'
              : 'bg-slate-800/80 text-slate-400 border-slate-700/50'
          }`}
        >
          {isDegreeMode ? 'DEG' : 'RAD'}
        </button>
        <button
          onClick={() => setIsSecondMode(!isSecondMode)}
          className={`flex-1 py-1.5 rounded-xl text-xs font-semibold border transition-all ${
            isSecondMode
              ? 'bg-indigo-500/25 text-indigo-300 border-indigo-500/40'
              : 'bg-slate-800/80 text-slate-400 border-slate-700/50'
          }`}
        >
          2nd
        </button>
        <button
          onClick={() => onInputChar('pi')}
          className="flex-1 py-1.5 rounded-xl text-xs font-semibold bg-slate-800/80 text-slate-300 border border-slate-700/50 hover:bg-slate-700/60 transition-all"
        >
          π
        </button>
        <button
          onClick={() => onInputChar('e')}
          className="flex-1 py-1.5 rounded-xl text-xs font-semibold bg-slate-800/80 text-slate-300 border border-slate-700/50 hover:bg-slate-700/60 transition-all"
        >
          e
        </button>
      </div>

      {/* Keypad Grid: 5 columns */}
      <div className="grid grid-cols-5 gap-1.5 flex-1">
        {/* Row 1: Functions */}
        <button
          onClick={() => onInputChar(isSecondMode ? 'asin(' : 'sin(')}
          className="h-11 sm:h-12 rounded-xl bg-slate-800/90 text-sky-300 border border-slate-700/50 text-xs sm:text-sm font-medium hover:bg-slate-700 active:scale-95 transition-all"
        >
          {isSecondMode ? 'sin⁻¹' : 'sin'}
        </button>
        <button
          onClick={() => onInputChar(isSecondMode ? 'acos(' : 'cos(')}
          className="h-11 sm:h-12 rounded-xl bg-slate-800/90 text-sky-300 border border-slate-700/50 text-xs sm:text-sm font-medium hover:bg-slate-700 active:scale-95 transition-all"
        >
          {isSecondMode ? 'cos⁻¹' : 'cos'}
        </button>
        <button
          onClick={() => onInputChar(isSecondMode ? 'atan(' : 'tan(')}
          className="h-11 sm:h-12 rounded-xl bg-slate-800/90 text-sky-300 border border-slate-700/50 text-xs sm:text-sm font-medium hover:bg-slate-700 active:scale-95 transition-all"
        >
          {isSecondMode ? 'tan⁻¹' : 'tan'}
        </button>
        <button
          onClick={onClear}
          className="h-11 sm:h-12 rounded-xl bg-rose-500/15 text-rose-400 border border-rose-500/20 text-xs sm:text-sm font-semibold hover:bg-rose-500/25 active:scale-95 transition-all"
        >
          AC
        </button>
        <button
          onClick={onDelete}
          className="h-11 sm:h-12 rounded-xl bg-slate-800/90 text-amber-400 border border-slate-700/50 flex items-center justify-center hover:bg-slate-700 active:scale-95 transition-all"
        >
          <Delete className="w-4 h-4" />
        </button>

        {/* Row 2 */}
        <button
          onClick={() => onInputChar('ln(')}
          className="h-11 sm:h-12 rounded-xl bg-slate-800/90 text-sky-300 border border-slate-700/50 text-xs sm:text-sm font-medium hover:bg-slate-700 active:scale-95 transition-all"
        >
          ln
        </button>
        <button
          onClick={() => onInputChar('log(')}
          className="h-11 sm:h-12 rounded-xl bg-slate-800/90 text-sky-300 border border-slate-700/50 text-xs sm:text-sm font-medium hover:bg-slate-700 active:scale-95 transition-all"
        >
          log
        </button>
        <button
          onClick={() => onInputChar('√(')}
          className="h-11 sm:h-12 rounded-xl bg-slate-800/90 text-sky-300 border border-slate-700/50 text-xs sm:text-sm font-medium hover:bg-slate-700 active:scale-95 transition-all"
        >
          √
        </button>
        <button
          onClick={() => onInputChar('(')}
          className="h-11 sm:h-12 rounded-xl bg-slate-800/90 text-sky-300 border border-slate-700/50 text-xs sm:text-sm font-medium hover:bg-slate-700 active:scale-95 transition-all"
        >
          (
        </button>
        <button
          onClick={() => onInputChar(')')}
          className="h-11 sm:h-12 rounded-xl bg-slate-800/90 text-sky-300 border border-slate-700/50 text-xs sm:text-sm font-medium hover:bg-slate-700 active:scale-95 transition-all"
        >
          )
        </button>

        {/* Row 3 */}
        <button
          onClick={() => onInputChar('^2')}
          className="h-11 sm:h-12 rounded-xl bg-slate-800/90 text-sky-300 border border-slate-700/50 text-xs sm:text-sm font-medium hover:bg-slate-700 active:scale-95 transition-all"
        >
          x²
        </button>
        <button
          onClick={() => onInputChar('^')}
          className="h-11 sm:h-12 rounded-xl bg-slate-800/90 text-sky-300 border border-slate-700/50 text-xs sm:text-sm font-medium hover:bg-slate-700 active:scale-95 transition-all"
        >
          xʸ
        </button>
        <button
          onClick={() => onInputChar('7')}
          className="h-11 sm:h-12 rounded-xl bg-slate-900/90 text-slate-100 border border-slate-800 text-base font-medium hover:bg-slate-800 active:scale-95 transition-all"
        >
          7
        </button>
        <button
          onClick={() => onInputChar('8')}
          className="h-11 sm:h-12 rounded-xl bg-slate-900/90 text-slate-100 border border-slate-800 text-base font-medium hover:bg-slate-800 active:scale-95 transition-all"
        >
          8
        </button>
        <button
          onClick={() => onInputChar('9')}
          className="h-11 sm:h-12 rounded-xl bg-slate-900/90 text-slate-100 border border-slate-800 text-base font-medium hover:bg-slate-800 active:scale-95 transition-all"
        >
          9
        </button>

        {/* Row 4 */}
        <button
          onClick={() => onInputChar('fact(')}
          className="h-11 sm:h-12 rounded-xl bg-slate-800/90 text-sky-300 border border-slate-700/50 text-xs sm:text-sm font-medium hover:bg-slate-700 active:scale-95 transition-all"
        >
          x!
        </button>
        <button
          onClick={() => onInputChar('÷')}
          className="h-11 sm:h-12 rounded-xl bg-indigo-500/20 text-indigo-300 border border-indigo-500/30 text-lg font-semibold hover:bg-indigo-500/30 active:scale-95 transition-all"
        >
          ÷
        </button>
        <button
          onClick={() => onInputChar('4')}
          className="h-11 sm:h-12 rounded-xl bg-slate-900/90 text-slate-100 border border-slate-800 text-base font-medium hover:bg-slate-800 active:scale-95 transition-all"
        >
          4
        </button>
        <button
          onClick={() => onInputChar('5')}
          className="h-11 sm:h-12 rounded-xl bg-slate-900/90 text-slate-100 border border-slate-800 text-base font-medium hover:bg-slate-800 active:scale-95 transition-all"
        >
          5
        </button>
        <button
          onClick={() => onInputChar('6')}
          className="h-11 sm:h-12 rounded-xl bg-slate-900/90 text-slate-100 border border-slate-800 text-base font-medium hover:bg-slate-800 active:scale-95 transition-all"
        >
          6
        </button>

        {/* Row 5 */}
        <button
          onClick={() => onInputChar('1/(')}
          className="h-11 sm:h-12 rounded-xl bg-slate-800/90 text-sky-300 border border-slate-700/50 text-xs sm:text-sm font-medium hover:bg-slate-700 active:scale-95 transition-all"
        >
          1/x
        </button>
        <button
          onClick={() => onInputChar('×')}
          className="h-11 sm:h-12 rounded-xl bg-indigo-500/20 text-indigo-300 border border-indigo-500/30 text-lg font-semibold hover:bg-indigo-500/30 active:scale-95 transition-all"
        >
          ×
        </button>
        <button
          onClick={() => onInputChar('1')}
          className="h-11 sm:h-12 rounded-xl bg-slate-900/90 text-slate-100 border border-slate-800 text-base font-medium hover:bg-slate-800 active:scale-95 transition-all"
        >
          1
        </button>
        <button
          onClick={() => onInputChar('2')}
          className="h-11 sm:h-12 rounded-xl bg-slate-900/90 text-slate-100 border border-slate-800 text-base font-medium hover:bg-slate-800 active:scale-95 transition-all"
        >
          2
        </button>
        <button
          onClick={() => onInputChar('3')}
          className="h-11 sm:h-12 rounded-xl bg-slate-900/90 text-slate-100 border border-slate-800 text-base font-medium hover:bg-slate-800 active:scale-95 transition-all"
        >
          3
        </button>

        {/* Row 6 */}
        <button
          onClick={() => onInputChar('%')}
          className="h-11 sm:h-12 rounded-xl bg-slate-800/90 text-sky-300 border border-slate-700/50 text-xs sm:text-sm font-medium hover:bg-slate-700 active:scale-95 transition-all"
        >
          %
        </button>
        <button
          onClick={() => onInputChar('−')}
          className="h-11 sm:h-12 rounded-xl bg-indigo-500/20 text-indigo-300 border border-indigo-500/30 text-lg font-semibold hover:bg-indigo-500/30 active:scale-95 transition-all"
        >
          −
        </button>
        <button
          onClick={onToggleSign}
          className="h-11 sm:h-12 rounded-xl bg-slate-800/80 text-slate-200 border border-slate-700/50 text-base font-medium hover:bg-slate-700 active:scale-95 transition-all"
        >
          ±
        </button>
        <button
          onClick={() => onInputChar('0')}
          className="h-11 sm:h-12 rounded-xl bg-slate-900/90 text-slate-100 border border-slate-800 text-base font-medium hover:bg-slate-800 active:scale-95 transition-all"
        >
          0
        </button>
        <button
          onClick={() => onInputChar('.')}
          className="h-11 sm:h-12 rounded-xl bg-slate-900/90 text-slate-100 border border-slate-800 text-base font-medium hover:bg-slate-800 active:scale-95 transition-all"
        >
          .
        </button>

        {/* Row 7 */}
        <button
          onClick={() => onInputChar('abs(')}
          className="h-11 sm:h-12 rounded-xl bg-slate-800/90 text-sky-300 border border-slate-700/50 text-xs sm:text-sm font-medium hover:bg-slate-700 active:scale-95 transition-all"
        >
          |x|
        </button>
        <button
          onClick={() => onInputChar('+')}
          className="h-11 sm:h-12 rounded-xl bg-indigo-500/20 text-indigo-300 border border-indigo-500/30 text-lg font-semibold hover:bg-indigo-500/30 active:scale-95 transition-all"
        >
          +
        </button>
        <button
          onClick={onEvaluate}
          className="col-span-3 h-11 sm:h-12 rounded-xl bg-gradient-to-r from-sky-500 to-indigo-600 text-white font-bold text-xl flex items-center justify-center hover:from-sky-400 hover:to-indigo-500 active:scale-95 transition-all shadow-md shadow-sky-500/20"
        >
          <Equal className="w-5 h-5" />
        </button>
      </div>
    </div>
  );
};
