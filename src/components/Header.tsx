import React from 'react';
import { CalculatorTab } from '../types';
import { 
  Calculator, 
  Binary, 
  ArrowLeftRight, 
  DollarSign, 
  Grid3X3, 
  History, 
  Settings 
} from 'lucide-react';

interface HeaderProps {
  currentTab: CalculatorTab;
  onSelectTab: (tab: CalculatorTab) => void;
  onOpenHistory: () => void;
  onOpenSettings: () => void;
  historyCount: number;
}

export const Header: React.FC<HeaderProps> = ({
  currentTab,
  onSelectTab,
  onOpenHistory,
  onOpenSettings,
  historyCount,
}) => {
  const navTabs: { id: CalculatorTab; label: string; icon: React.ReactNode }[] = [
    { id: 'standard', label: 'Standard', icon: <Calculator className="w-4 h-4" /> },
    { id: 'scientific', label: 'Scientific', icon: <Binary className="w-4 h-4" /> },
    { id: 'converter', label: 'Units', icon: <ArrowLeftRight className="w-4 h-4" /> },
    { id: 'currency', label: 'Currency', icon: <DollarSign className="w-4 h-4" /> },
    { id: 'matrix', label: 'Matrix', icon: <Grid3X3 className="w-4 h-4" /> },
  ];

  return (
    <header className="w-full flex flex-col border-b border-slate-800 bg-slate-900/80 backdrop-blur-md sticky top-0 z-30">
      <div className="flex items-center justify-between px-4 py-3">
        <div className="flex items-center space-x-2.5">
          <div className="w-8 h-8 rounded-xl bg-gradient-to-tr from-sky-500 to-indigo-500 flex items-center justify-center shadow-md shadow-sky-500/20">
            <Calculator className="w-4 h-4 text-white" />
          </div>
          <div>
            <h1 className="text-lg font-bold tracking-tight text-white flex items-center gap-1.5">
              Calculator
            </h1>
          </div>
        </div>

        <div className="flex items-center space-x-1.5">
          <button
            onClick={onOpenHistory}
            className="relative p-2 rounded-xl text-slate-300 hover:text-white hover:bg-slate-800/80 transition-all"
            title="Calculation History"
          >
            <History className="w-5 h-5" />
            {historyCount > 0 && (
              <span className="absolute top-1 right-1 w-2 h-2 bg-sky-400 rounded-full animate-pulse" />
            )}
          </button>

          <button
            onClick={onOpenSettings}
            className="p-2 rounded-xl text-slate-300 hover:text-white hover:bg-slate-800/80 transition-all"
            title="Settings & Themes"
          >
            <Settings className="w-5 h-5" />
          </button>
        </div>
      </div>

      {/* Tabs navigation */}
      <div className="flex items-center space-x-1 px-3 pb-2 overflow-x-auto no-scrollbar">
        {navTabs.map((tab) => {
          const isActive = currentTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => onSelectTab(tab.id)}
              className={`flex items-center space-x-1.5 px-3 py-1.5 rounded-lg text-xs font-medium whitespace-nowrap transition-all ${
                isActive
                  ? 'bg-sky-500/20 text-sky-300 border border-sky-500/30 font-semibold shadow-sm'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/50'
              }`}
            >
              {tab.icon}
              <span>{tab.label}</span>
            </button>
          );
        })}
      </div>
    </header>
  );
};
