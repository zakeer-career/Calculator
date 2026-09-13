import React, { useState } from 'react';
import { HistoryItem } from '../types';
import { X, Trash2, Search, Copy, Check } from 'lucide-react';

interface HistoryDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  history: HistoryItem[];
  onSelectHistory: (item: HistoryItem) => void;
  onClearHistory: () => void;
}

export const HistoryDrawer: React.FC<HistoryDrawerProps> = ({
  isOpen,
  onClose,
  history,
  onSelectHistory,
  onClearHistory,
}) => {
  const [search, setSearch] = useState('');
  const [copiedId, setCopiedId] = useState<string | null>(null);

  if (!isOpen) return null;

  const filtered = history.filter(
    (item) =>
      item.expression.toLowerCase().includes(search.toLowerCase()) ||
      item.result.toLowerCase().includes(search.toLowerCase())
  );

  const handleCopy = (id: string, text: string) => {
    navigator.clipboard.writeText(text);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 1500);
  };

  return (
    <div className="fixed inset-0 z-50 flex justify-end bg-black/60 backdrop-blur-sm animate-fadeIn">
      <div className="w-full max-w-sm h-full bg-slate-900 border-l border-slate-800 flex flex-col p-4 shadow-2xl">
        {/* Header */}
        <div className="flex items-center justify-between pb-3 border-b border-slate-800 mb-3">
          <h3 className="text-lg font-bold text-white">Calculation History</h3>
          <div className="flex items-center space-x-1">
            {history.length > 0 && (
              <button
                onClick={onClearHistory}
                className="p-2 rounded-xl text-rose-400 hover:bg-rose-500/15 transition-all"
                title="Clear All History"
              >
                <Trash2 className="w-4 h-4" />
              </button>
            )}
            <button
              onClick={onClose}
              className="p-2 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800 transition-all"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Search */}
        <div className="relative mb-3">
          <Search className="w-4 h-4 absolute left-3 top-3 text-slate-500" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search calculations..."
            className="w-full bg-slate-800 border border-slate-700 rounded-xl pl-9 pr-3 py-2 text-sm text-white placeholder-slate-500 outline-none focus:border-sky-500"
          />
        </div>

        {/* List */}
        <div className="flex-1 overflow-y-auto space-y-2.5 pr-1">
          {filtered.length === 0 ? (
            <div className="h-48 flex flex-col items-center justify-center text-slate-500 text-sm">
              <span>No calculations yet</span>
            </div>
          ) : (
            filtered.map((item) => (
              <div
                key={item.id}
                className="p-3 rounded-2xl bg-slate-800/60 border border-slate-700/50 hover:border-slate-600 transition-all group"
              >
                <div
                  onClick={() => {
                    onSelectHistory(item);
                    onClose();
                  }}
                  className="cursor-pointer"
                >
                  <div className="text-xs font-mono text-slate-400 truncate">
                    {item.expression}
                  </div>
                  <div className="text-lg font-mono font-bold text-sky-400 mt-1 truncate">
                    = {item.result}
                  </div>
                  <div className="text-[10px] text-slate-500 mt-1">
                    {new Date(item.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  </div>
                </div>

                <div className="flex items-center justify-end space-x-2 mt-2 pt-2 border-t border-slate-700/30">
                  <button
                    onClick={() => handleCopy(item.id, item.result)}
                    className="text-xs text-slate-400 hover:text-sky-300 flex items-center gap-1"
                  >
                    {copiedId === item.id ? (
                      <>
                        <Check className="w-3 h-3 text-emerald-400" />
                        <span className="text-[11px] text-emerald-400">Copied</span>
                      </>
                    ) : (
                      <>
                        <Copy className="w-3 h-3" />
                        <span className="text-[11px]">Copy Result</span>
                      </>
                    )}
                  </button>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};
