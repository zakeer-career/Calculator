import React, { useState, useEffect, useCallback } from 'react';
import { CalculatorTab, HistoryItem, AppThemePreset, NumberFormatStyle } from './types';
import { MathEvaluator } from './utils/mathEvaluator';
import { Header } from './components/Header';
import { StandardCalculator } from './components/StandardCalculator';
import { ScientificCalculator } from './components/ScientificCalculator';
import { UnitConverterView } from './components/UnitConverterView';
import { CurrencyConverterView } from './components/CurrencyConverterView';
import { MatrixCalculatorView } from './components/MatrixCalculatorView';
import { HistoryDrawer } from './components/HistoryDrawer';
import { SettingsModal } from './components/SettingsModal';

export const App: React.FC = () => {
  const [currentTab, setCurrentTab] = useState<CalculatorTab>('standard');
  const [expression, setExpression] = useState<string>('');
  const [previewResult, setPreviewResult] = useState<string>('');
  const [isDegreeMode, setIsDegreeMode] = useState<boolean>(true);

  // Settings states
  const [theme, setTheme] = useState<AppThemePreset>(() => {
    return (localStorage.getItem('calc_theme') as AppThemePreset) || 'liquid_glass';
  });
  const [numberFormat, setNumberFormat] = useState<NumberFormatStyle>(() => {
    return (localStorage.getItem('calc_num_format') as NumberFormatStyle) || 'STANDARD';
  });
  const [precision, setPrecision] = useState<number>(() => {
    const saved = localStorage.getItem('calc_precision');
    return saved !== null ? parseInt(saved, 10) : -1;
  });
  const [soundEnabled, setSoundEnabled] = useState<boolean>(() => {
    return localStorage.getItem('calc_sound') === 'true';
  });

  // History state
  const [history, setHistory] = useState<HistoryItem[]>(() => {
    try {
      const saved = localStorage.getItem('calc_history');
      return saved ? JSON.parse(saved) : [];
    } catch {
      return [];
    }
  });

  const [isHistoryOpen, setIsHistoryOpen] = useState(false);
  const [isSettingsOpen, setIsSettingsOpen] = useState(false);

  // Save history
  useEffect(() => {
    localStorage.setItem('calc_history', JSON.stringify(history));
  }, [history]);

  // Save settings
  useEffect(() => {
    localStorage.setItem('calc_theme', theme);
  }, [theme]);
  useEffect(() => {
    localStorage.setItem('calc_num_format', numberFormat);
  }, [numberFormat]);
  useEffect(() => {
    localStorage.setItem('calc_precision', precision.toString());
  }, [precision]);
  useEffect(() => {
    localStorage.setItem('calc_sound', soundEnabled.toString());
  }, [soundEnabled]);

  // Audio feedback
  const playClickSound = useCallback(() => {
    if (!soundEnabled) return;
    try {
      const AudioCtx = window.AudioContext || (window as any).webkitAudioContext;
      if (AudioCtx) {
        const ctx = new AudioCtx();
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.type = 'sine';
        osc.frequency.setValueAtTime(500, ctx.currentTime);
        osc.frequency.exponentialRampToValueAtTime(300, ctx.currentTime + 0.04);
        gain.gain.setValueAtTime(0.08, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.04);
        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start();
        osc.stop(ctx.currentTime + 0.04);
      }
    } catch {
      // Ignore audio failure
    }
  }, [soundEnabled]);

  // Live partial evaluation
  useEffect(() => {
    if (!expression || expression.trim() === '') {
      setPreviewResult('');
      return;
    }
    const res = MathEvaluator.evaluatePartial(expression, isDegreeMode, precision, numberFormat);
    if (res.success && res.formatted !== expression) {
      setPreviewResult(res.formatted);
    } else {
      setPreviewResult('');
    }
  }, [expression, isDegreeMode, precision, numberFormat]);

  const handleInputChar = (char: string) => {
    playClickSound();
    setExpression((prev) => {
      // Prevent multiple leading zeros
      if (prev === '0' && char !== '.' && !['+', '−', '×', '÷', '%', '^'].includes(char)) {
        return char;
      }
      return prev + char;
    });
  };

  const handleClear = () => {
    playClickSound();
    setExpression('');
    setPreviewResult('');
  };

  const handleDelete = () => {
    playClickSound();
    setExpression((prev) => prev.slice(0, -1));
  };

  const handleToggleSign = () => {
    playClickSound();
    setExpression((prev) => {
      if (!prev) return '−';
      if (prev.startsWith('−')) return prev.substring(1);
      if (prev.startsWith('-')) return prev.substring(1);
      return '−' + prev;
    });
  };

  const handleEvaluate = () => {
    playClickSound();
    if (!expression || expression.trim() === '') return;

    const res = MathEvaluator.evaluate(expression, isDegreeMode, precision, numberFormat);
    if (res.success) {
      const newItem: HistoryItem = {
        id: Date.now().toString(),
        expression,
        result: res.formatted,
        timestamp: Date.now(),
      };
      setHistory((prev) => [newItem, ...prev.slice(0, 49)]);
      setExpression(res.formatted);
      setPreviewResult('');
    } else {
      setPreviewResult(res.error || 'Error');
    }
  };

  // Keyboard support
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      // If typing in input on converters or matrix, don't hijack
      if (['INPUT', 'SELECT', 'TEXTAREA'].includes((e.target as HTMLElement)?.tagName)) {
        return;
      }

      if (e.key >= '0' && e.key <= '9') {
        handleInputChar(e.key);
      } else if (e.key === '.') {
        handleInputChar('.');
      } else if (e.key === '+') {
        handleInputChar('+');
      } else if (e.key === '-') {
        handleInputChar('−');
      } else if (e.key === '*') {
        handleInputChar('×');
      } else if (e.key === '/') {
        e.preventDefault();
        handleInputChar('÷');
      } else if (e.key === '(' || e.key === ')') {
        handleInputChar(e.key);
      } else if (e.key === '%') {
        handleInputChar('%');
      } else if (e.key === '^') {
        handleInputChar('^');
      } else if (e.key === 'Enter' || e.key === '=') {
        e.preventDefault();
        handleEvaluate();
      } else if (e.key === 'Backspace') {
        handleDelete();
      } else if (e.key === 'Escape') {
        handleClear();
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [expression, isDegreeMode, precision, numberFormat]);

  // Theme styling classes
  const getThemeBackground = () => {
    switch (theme) {
      case 'material_you':
        return 'bg-gradient-to-b from-indigo-950 via-slate-950 to-slate-900';
      case 'amoled':
        return 'bg-black';
      case 'nord':
        return 'bg-gradient-to-b from-cyan-950 via-slate-950 to-slate-900';
      case 'emerald':
        return 'bg-gradient-to-b from-emerald-950 via-slate-950 to-slate-900';
      case 'liquid_glass':
      default:
        return 'bg-gradient-to-b from-slate-950 via-slate-900 to-slate-950';
    }
  };

  return (
    <div className={`min-h-screen w-full flex flex-col ${getThemeBackground()} text-slate-100 transition-colors duration-300`}>
      <Header
        currentTab={currentTab}
        onSelectTab={setCurrentTab}
        onOpenHistory={() => setIsHistoryOpen(true)}
        onOpenSettings={() => setIsSettingsOpen(true)}
        historyCount={history.length}
      />

      <main className="flex-1 flex flex-col justify-center items-center overflow-y-auto">
        {currentTab === 'standard' && (
          <StandardCalculator
            expression={expression}
            previewResult={previewResult}
            onInputChar={handleInputChar}
            onClear={handleClear}
            onDelete={handleDelete}
            onEvaluate={handleEvaluate}
            onToggleSign={handleToggleSign}
          />
        )}

        {currentTab === 'scientific' && (
          <ScientificCalculator
            expression={expression}
            previewResult={previewResult}
            isDegreeMode={isDegreeMode}
            onToggleDegreeMode={() => setIsDegreeMode(!isDegreeMode)}
            onInputChar={handleInputChar}
            onClear={handleClear}
            onDelete={handleDelete}
            onEvaluate={handleEvaluate}
            onToggleSign={handleToggleSign}
          />
        )}

        {currentTab === 'converter' && <UnitConverterView />}

        {currentTab === 'currency' && <CurrencyConverterView />}

        {currentTab === 'matrix' && <MatrixCalculatorView />}
      </main>

      <HistoryDrawer
        isOpen={isHistoryOpen}
        onClose={() => setIsHistoryOpen(false)}
        history={history}
        onSelectHistory={(item) => {
          setExpression(item.expression);
        }}
        onClearHistory={() => setHistory([])}
      />

      <SettingsModal
        isOpen={isSettingsOpen}
        onClose={() => setIsSettingsOpen(false)}
        theme={theme}
        onSelectTheme={setTheme}
        numberFormat={numberFormat}
        onSelectNumberFormat={setNumberFormat}
        precision={precision}
        onSelectPrecision={setPrecision}
        soundEnabled={soundEnabled}
        onToggleSound={() => setSoundEnabled(!soundEnabled)}
      />
    </div>
  );
};
export default App;
