import React, { useState, useEffect } from 'react';
import { UnitCategory, UnitItem } from '../types';
import { UNIT_CATEGORIES, UNIT_DATA, convertUnits } from '../utils/unitConverter';
import { ArrowLeftRight, Copy, Check } from 'lucide-react';

export const UnitConverterView: React.FC = () => {
  const [category, setCategory] = useState<UnitCategory>('Length');
  const [fromUnit, setFromUnit] = useState<UnitItem>(UNIT_DATA['Length'][0]);
  const [toUnit, setToUnit] = useState<UnitItem>(UNIT_DATA['Length'][1]);
  const [fromValue, setFromValue] = useState<string>('1');
  const [toValue, setToValue] = useState<string>('');
  const [copied, setCopied] = useState(false);

  useEffect(() => {
    const units = UNIT_DATA[category];
    setFromUnit(units[0]);
    setToUnit(units[1] || units[0]);
  }, [category]);

  useEffect(() => {
    const num = parseFloat(fromValue);
    if (isNaN(num)) {
      setToValue('');
    } else {
      const res = convertUnits(category, fromUnit, toUnit, num);
      // Clean display
      setToValue(parseFloat(res.toPrecision(8)).toString());
    }
  }, [category, fromUnit, toUnit, fromValue]);

  const handleSwap = () => {
    const prevFrom = fromUnit;
    setFromUnit(toUnit);
    setToUnit(prevFrom);
    if (toValue) setFromValue(toValue);
  };

  const handleCopy = () => {
    if (!toValue) return;
    navigator.clipboard.writeText(`${toValue} ${toUnit.symbol}`);
    setCopied(true);
    setTimeout(() => setCopied(false), 1500);
  };

  const availableUnits = UNIT_DATA[category];

  return (
    <div className="flex flex-col h-full max-w-md mx-auto w-full p-4 overflow-y-auto">
      <h2 className="text-xl font-bold text-white mb-3 flex items-center justify-between">
        <span>Unit Converter</span>
        <button
          onClick={handleSwap}
          className="p-2 rounded-xl bg-slate-800 text-sky-400 hover:bg-slate-700 active:scale-95 transition-all text-xs font-semibold flex items-center gap-1.5"
        >
          <ArrowLeftRight className="w-3.5 h-3.5" />
          <span>Swap</span>
        </button>
      </h2>

      {/* Category Horizontal Scroll */}
      <div className="flex items-center space-x-1.5 pb-2 mb-4 overflow-x-auto no-scrollbar">
        {UNIT_CATEGORIES.map((cat) => (
          <button
            key={cat}
            onClick={() => setCategory(cat)}
            className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all ${
              category === cat
                ? 'bg-sky-500 text-white shadow-md shadow-sky-500/25'
                : 'bg-slate-800/80 text-slate-300 hover:bg-slate-700/60'
            }`}
          >
            {cat}
          </button>
        ))}
      </div>

      {/* Input Cards */}
      <div className="space-y-3">
        {/* From Box */}
        <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-sm">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-medium text-slate-400 uppercase tracking-wider">From</span>
            <select
              value={fromUnit.name}
              onChange={(e) => {
                const u = availableUnits.find((item) => item.name === e.target.value);
                if (u) setFromUnit(u);
              }}
              className="bg-slate-800 border border-slate-700 text-sky-400 text-sm rounded-lg px-2.5 py-1 outline-none font-medium"
            >
              {availableUnits.map((u) => (
                <option key={u.name} value={u.name}>
                  {u.name} ({u.symbol})
                </option>
              ))}
            </select>
          </div>
          <input
            type="number"
            value={fromValue}
            onChange={(e) => setFromValue(e.target.value)}
            className="w-full bg-transparent text-white font-mono text-3xl font-bold outline-none"
            placeholder="0"
          />
        </div>

        {/* To Box */}
        <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-sm relative">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-medium text-slate-400 uppercase tracking-wider">To</span>
            <select
              value={toUnit.name}
              onChange={(e) => {
                const u = availableUnits.find((item) => item.name === e.target.value);
                if (u) setToUnit(u);
              }}
              className="bg-slate-800 border border-slate-700 text-sky-400 text-sm rounded-lg px-2.5 py-1 outline-none font-medium"
            >
              {availableUnits.map((u) => (
                <option key={u.name} value={u.name}>
                  {u.name} ({u.symbol})
                </option>
              ))}
            </select>
          </div>
          <div className="flex items-center justify-between">
            <div className="text-white font-mono text-3xl font-bold truncate">
              {toValue || '0'}
            </div>
            <button
              onClick={handleCopy}
              className="p-2 rounded-xl bg-slate-800 text-slate-300 hover:text-white hover:bg-slate-700 transition-all flex items-center gap-1 text-xs"
              title="Copy Result"
            >
              {copied ? <Check className="w-4 h-4 text-emerald-400" /> : <Copy className="w-4 h-4" />}
            </button>
          </div>
        </div>
      </div>

      {/* Quick Numbers Pad */}
      <div className="mt-5 grid grid-cols-3 gap-2">
        {['1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '0', 'C'].map((val) => (
          <button
            key={val}
            onClick={() => {
              if (val === 'C') {
                setFromValue('');
              } else if (val === '.') {
                if (!fromValue.includes('.')) setFromValue((prev) => prev + '.');
              } else {
                setFromValue((prev) => (prev === '0' ? val : prev + val));
              }
            }}
            className="py-3 rounded-xl bg-slate-800/80 text-white font-mono text-lg font-semibold hover:bg-slate-700 active:scale-95 transition-all shadow-sm"
          >
            {val}
          </button>
        ))}
      </div>
    </div>
  );
};
