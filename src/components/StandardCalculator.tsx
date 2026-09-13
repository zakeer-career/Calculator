import React from 'react';
import { Delete, Equal } from 'lucide-react';

interface StandardCalculatorProps {
  expression: string;
  previewResult: string;
  onInputChar: (char: string) => void;
  onClear: () => void;
  onDelete: () => void;
  onEvaluate: () => void;
  onToggleSign: () => void;
}

export const StandardCalculator: React.FC<StandardCalculatorProps> = ({
  expression,
  previewResult,
  onInputChar,
  onClear,
  onDelete,
  onEvaluate,
  onToggleSign,
}) => {
  return (
    <div className="flex flex-col h-full max-w-md mx-auto w-full p-3 select-none">
      {/* Display Screen */}
      <div className="flex flex-col justify-end items-end p-5 mb-4 rounded-3xl bg-slate-900/90 border border-slate-800 shadow-inner min-h-[140px] max-h-[180px] overflow-hidden">
        <div className="w-full text-right font-mono text-slate-400 text-lg md:text-xl tracking-wide overflow-x-auto whitespace-nowrap scrollbar-none">
          {expression || '0'}
        </div>
        <div className="w-full text-right font-mono text-3xl md:text-4xl font-bold tracking-tight text-white mt-2 overflow-x-auto whitespace-nowrap scrollbar-none">
          {previewResult ? `= ${previewResult}` : (expression ? '' : '0')}
        </div>
      </div>

      {/* Keypad Grid */}
      <div className="grid grid-cols-4 gap-2.5 flex-1">
        {/* Row 1 */}
        <button
          onClick={onClear}
          className="h-14 sm:h-16 rounded-2xl bg-rose-500/15 text-rose-400 border border-rose-500/20 font-semibold text-lg hover:bg-rose-500/25 active:scale-95 transition-all shadow-sm"
        >
          AC
        </button>
        <button
          onClick={() => onInputChar('(')}
          className="h-14 sm:h-16 rounded-2xl bg-slate-800/80 text-sky-400 border border-slate-700/50 font-medium text-lg hover:bg-slate-700/60 active:scale-95 transition-all shadow-sm"
        >
          (
        </button>
        <button
          onClick={() => onInputChar(')')}
          className="h-14 sm:h-16 rounded-2xl bg-slate-800/80 text-sky-400 border border-slate-700/50 font-medium text-lg hover:bg-slate-700/60 active:scale-95 transition-all shadow-sm"
        >
          )
        </button>
        <button
          onClick={onDelete}
          className="h-14 sm:h-16 rounded-2xl bg-slate-800/80 text-amber-400 border border-slate-700/50 flex items-center justify-center hover:bg-slate-700/60 active:scale-95 transition-all shadow-sm"
          title="Backspace"
        >
          <Delete className="w-5 h-5" />
        </button>

        {/* Row 2 */}
        <button
          onClick={() => onInputChar('%')}
          className="h-14 sm:h-16 rounded-2xl bg-slate-800/80 text-sky-400 border border-slate-700/50 font-medium text-lg hover:bg-slate-700/60 active:scale-95 transition-all shadow-sm"
        >
          %
        </button>
        <button
          onClick={() => onInputChar('^')}
          className="h-14 sm:h-16 rounded-2xl bg-slate-800/80 text-sky-400 border border-slate-700/50 font-medium text-lg hover:bg-slate-700/60 active:scale-95 transition-all shadow-sm"
        >
          xʸ
        </button>
        <button
          onClick={() => onInputChar('√(')}
          className="h-14 sm:h-16 rounded-2xl bg-slate-800/80 text-sky-400 border border-slate-700/50 font-medium text-lg hover:bg-slate-700/60 active:scale-95 transition-all shadow-sm"
        >
          √
        </button>
        <button
          onClick={() => onInputChar('÷')}
          className="h-14 sm:h-16 rounded-2xl bg-indigo-500/20 text-indigo-300 border border-indigo-500/30 font-semibold text-2xl hover:bg-indigo-500/30 active:scale-95 transition-all shadow-sm"
        >
          ÷
        </button>

        {/* Row 3 */}
        <button
          onClick={() => onInputChar('7')}
          className="h-14 sm:h-16 rounded-2xl bg-slate-900/90 text-slate-100 border border-slate-800 font-medium text-xl hover:bg-slate-800 active:scale-95 transition-all shadow-sm"
        >
          7
        </button>
        <button
          onClick={() => onInputChar('8')}
          className="h-14 sm:h-16 rounded-2xl bg-slate-900/90 text-slate-100 border border-slate-800 font-medium text-xl hover:bg-slate-800 active:scale-95 transition-all shadow-sm"
        >
          8
        </button>
        <button
          onClick={() => onInputChar('9')}
          className="h-14 sm:h-16 rounded-2xl bg-slate-900/90 text-slate-100 border border-slate-800 font-medium text-xl hover:bg-slate-800 active:scale-95 transition-all shadow-sm"
        >
          9
        </button>
        <button
          onClick={() => onInputChar('×')}
          className="h-14 sm:h-16 rounded-2xl bg-indigo-500/20 text-indigo-300 border border-indigo-500/30 font-semibold text-2xl hover:bg-indigo-500/30 active:scale-95 transition-all shadow-sm"
        >
          ×
        </button>

        {/* Row 4 */}
        <button
          onClick={() => onInputChar('4')}
          className="h-14 sm:h-16 rounded-2xl bg-slate-900/90 text-slate-100 border border-slate-800 font-medium text-xl hover:bg-slate-800 active:scale-95 transition-all shadow-sm"
        >
          4
        </button>
        <button
          onClick={() => onInputChar('5')}
          className="h-14 sm:h-16 rounded-2xl bg-slate-900/90 text-slate-100 border border-slate-800 font-medium text-xl hover:bg-slate-800 active:scale-95 transition-all shadow-sm"
        >
          5
        </button>
        <button
          onClick={() => onInputChar('6')}
          className="h-14 sm:h-16 rounded-2xl bg-slate-900/90 text-slate-100 border border-slate-800 font-medium text-xl hover:bg-slate-800 active:scale-95 transition-all shadow-sm"
        >
          6
        </button>
        <button
          onClick={() => onInputChar('−')}
          className="h-14 sm:h-16 rounded-2xl bg-indigo-500/20 text-indigo-300 border border-indigo-500/30 font-semibold text-2xl hover:bg-indigo-500/30 active:scale-95 transition-all shadow-sm"
        >
          −
        </button>

        {/* Row 5 */}
        <button
          onClick={() => onInputChar('1')}
          className="h-14 sm:h-16 rounded-2xl bg-slate-900/90 text-slate-100 border border-slate-800 font-medium text-xl hover:bg-slate-800 active:scale-95 transition-all shadow-sm"
        >
          1
        </button>
        <button
          onClick={() => onInputChar('2')}
          className="h-14 sm:h-16 rounded-2xl bg-slate-900/90 text-slate-100 border border-slate-800 font-medium text-xl hover:bg-slate-800 active:scale-95 transition-all shadow-sm"
        >
          2
        </button>
        <button
          onClick={() => onInputChar('3')}
          className="h-14 sm:h-16 rounded-2xl bg-slate-900/90 text-slate-100 border border-slate-800 font-medium text-xl hover:bg-slate-800 active:scale-95 transition-all shadow-sm"
        >
          3
        </button>
        <button
          onClick={() => onInputChar('+')}
          className="h-14 sm:h-16 rounded-2xl bg-indigo-500/20 text-indigo-300 border border-indigo-500/30 font-semibold text-2xl hover:bg-indigo-500/30 active:scale-95 transition-all shadow-sm"
        >
          +
        </button>

        {/* Row 6 */}
        <button
          onClick={onToggleSign}
          className="h-14 sm:h-16 rounded-2xl bg-slate-800/80 text-slate-200 border border-slate-700/50 font-medium text-lg hover:bg-slate-700/60 active:scale-95 transition-all shadow-sm"
        >
          ±
        </button>
        <button
          onClick={() => onInputChar('0')}
          className="h-14 sm:h-16 rounded-2xl bg-slate-900/90 text-slate-100 border border-slate-800 font-medium text-xl hover:bg-slate-800 active:scale-95 transition-all shadow-sm"
        >
          0
        </button>
        <button
          onClick={() => onInputChar('.')}
          className="h-14 sm:h-16 rounded-2xl bg-slate-900/90 text-slate-100 border border-slate-800 font-medium text-xl hover:bg-slate-800 active:scale-95 transition-all shadow-sm"
        >
          .
        </button>
        <button
          onClick={onEvaluate}
          className="h-14 sm:h-16 rounded-2xl bg-gradient-to-r from-sky-500 to-indigo-600 text-white font-bold text-2xl flex items-center justify-center hover:from-sky-400 hover:to-indigo-500 active:scale-95 transition-all shadow-lg shadow-sky-500/25"
        >
          <Equal className="w-6 h-6" />
        </button>
      </div>
    </div>
  );
};
