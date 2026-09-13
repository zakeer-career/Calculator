import React from 'react';
import { AppThemePreset, NumberFormatStyle } from '../types';
import { X, Check } from 'lucide-react';

interface SettingsModalProps {
  isOpen: boolean;
  onClose: () => void;
  theme: AppThemePreset;
  onSelectTheme: (t: AppThemePreset) => void;
  numberFormat: NumberFormatStyle;
  onSelectNumberFormat: (f: NumberFormatStyle) => void;
  precision: number;
  onSelectPrecision: (p: number) => void;
  soundEnabled: boolean;
  onToggleSound: () => void;
}

export const SettingsModal: React.FC<SettingsModalProps> = ({
  isOpen,
  onClose,
  theme,
  onSelectTheme,
  numberFormat,
  onSelectNumberFormat,
  precision,
  onSelectPrecision,
  soundEnabled,
  onToggleSound,
}) => {
  if (!isOpen) return null;

  const themes: { id: AppThemePreset; name: string; color: string }[] = [
    { id: 'liquid_glass', name: 'Liquid Glass (Default)', color: 'bg-sky-500' },
    { id: 'material_you', name: 'Material You', color: 'bg-indigo-500' },
    { id: 'amoled', name: 'AMOLED Midnight', color: 'bg-slate-950' },
    { id: 'nord', name: 'Nord Cyan', color: 'bg-cyan-500' },
    { id: 'emerald', name: 'Emerald Forest', color: 'bg-emerald-500' },
  ];

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fadeIn">
      <div className="w-full max-w-md bg-slate-900 border border-slate-800 rounded-3xl p-5 shadow-2xl flex flex-col max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-center justify-between pb-3 border-b border-slate-800 mb-4">
          <h3 className="text-lg font-bold text-white">Settings</h3>
          <button
            onClick={onClose}
            className="p-2 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800 transition-all"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Themes */}
        <div className="mb-5">
          <div className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-2.5">
            Theme Presets
          </div>
          <div className="grid grid-cols-1 gap-2">
            {themes.map((t) => (
              <button
                key={t.id}
                onClick={() => onSelectTheme(t.id)}
                className={`flex items-center justify-between p-3 rounded-xl border transition-all ${
                  theme === t.id
                    ? 'bg-sky-500/20 border-sky-500/40 text-white'
                    : 'bg-slate-800/60 border-slate-700/50 text-slate-300 hover:bg-slate-800'
                }`}
              >
                <div className="flex items-center space-x-2.5">
                  <div className={`w-4 h-4 rounded-full ${t.color} border border-white/20`} />
                  <span className="text-sm font-medium">{t.name}</span>
                </div>
                {theme === t.id && <Check className="w-4 h-4 text-sky-400" />}
              </button>
            ))}
          </div>
        </div>

        {/* Number Format */}
        <div className="mb-5">
          <div className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-2.5">
            Number Separators
          </div>
          <div className="grid grid-cols-3 gap-2">
            {[
              { id: 'STANDARD', label: '1,234.56', name: 'Standard' },
              { id: 'EUROPEAN', label: '1.234,56', name: 'European' },
              { id: 'INDIAN', label: '1,23,456', name: 'Indian' },
            ].map((fmt) => (
              <button
                key={fmt.id}
                onClick={() => onSelectNumberFormat(fmt.id as NumberFormatStyle)}
                className={`p-2.5 rounded-xl border text-center transition-all ${
                  numberFormat === fmt.id
                    ? 'bg-sky-500/20 border-sky-500/40 text-sky-300 font-semibold'
                    : 'bg-slate-800/60 border-slate-700/50 text-slate-400 hover:bg-slate-800'
                }`}
              >
                <div className="text-xs font-bold">{fmt.name}</div>
                <div className="text-[11px] font-mono mt-0.5">{fmt.label}</div>
              </button>
            ))}
          </div>
        </div>

        {/* Precision */}
        <div className="mb-5">
          <div className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-2.5">
            Decimal Precision
          </div>
          <div className="grid grid-cols-4 gap-2">
            {[
              { val: -1, label: 'Auto' },
              { val: 2, label: '2 dec' },
              { val: 4, label: '4 dec' },
              { val: 6, label: '6 dec' },
            ].map((p) => (
              <button
                key={p.val}
                onClick={() => onSelectPrecision(p.val)}
                className={`py-2 rounded-xl border text-xs font-semibold transition-all ${
                  precision === p.val
                    ? 'bg-sky-500/20 border-sky-500/40 text-sky-300'
                    : 'bg-slate-800/60 border-slate-700/50 text-slate-400 hover:bg-slate-800'
                }`}
              >
                {p.label}
              </button>
            ))}
          </div>
        </div>

        {/* Feedback / Sound */}
        <div className="mb-5 flex items-center justify-between p-3 rounded-2xl bg-slate-800/60 border border-slate-700/50">
          <div>
            <div className="text-sm font-semibold text-white">Audio Click Tone</div>
            <div className="text-xs text-slate-400">Play soft click feedback on keypad</div>
          </div>
          <button
            onClick={onToggleSound}
            className={`w-12 h-6 flex items-center rounded-full p-1 transition-all ${
              soundEnabled ? 'bg-sky-500' : 'bg-slate-700'
            }`}
          >
            <div
              className={`bg-white w-4 h-4 rounded-full shadow-md transform transition-transform ${
                soundEnabled ? 'translate-x-6' : 'translate-x-0'
              }`}
            />
          </button>
        </div>

        {/* About info */}
        <div className="text-center text-xs text-slate-500 pt-3 border-t border-slate-800">
          Calculator • Calz Edition v1.0.0
        </div>
      </div>
    </div>
  );
};
