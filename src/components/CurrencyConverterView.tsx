import React, { useState, useEffect } from 'react';
import { DEFAULT_CURRENCIES, fetchLiveRates } from '../utils/currencyData';
import { CurrencyRate } from '../types';
import { ArrowLeftRight, RefreshCw, Check, Copy } from 'lucide-react';

export const CurrencyConverterView: React.FC = () => {
  const [currencies, setCurrencies] = useState<CurrencyRate[]>(DEFAULT_CURRENCIES);
  const [fromCurrency, setFromCurrency] = useState<CurrencyRate>(DEFAULT_CURRENCIES[0]); // USD
  const [toCurrency, setToCurrency] = useState<CurrencyRate>(DEFAULT_CURRENCIES[1]); // EUR
  const [amount, setAmount] = useState<string>('100');
  const [convertedAmount, setConvertedAmount] = useState<string>('');
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [lastUpdated, setLastUpdated] = useState<string>('Default rates');
  const [copied, setCopied] = useState(false);

  // Load live rates on mount
  useEffect(() => {
    handleRefreshRates();
  }, []);

  const handleRefreshRates = async () => {
    setIsRefreshing(true);
    const rates = await fetchLiveRates();
    if (rates) {
      setCurrencies((prev) =>
        prev.map((c) => ({
          ...c,
          rateAgainstUSD: rates[c.code] || c.rateAgainstUSD,
        }))
      );
      setLastUpdated('Updated just now');
    } else {
      setLastUpdated('Using cached rates');
    }
    setIsRefreshing(false);
  };

  useEffect(() => {
    const num = parseFloat(amount);
    if (isNaN(num)) {
      setConvertedAmount('');
      return;
    }

    // Convert from -> USD -> to
    // rateAgainstUSD means: 1 USD = rateAgainstUSD [code]
    const amountInUSD = num / fromCurrency.rateAgainstUSD;
    const result = amountInUSD * toCurrency.rateAgainstUSD;
    setConvertedAmount(result.toFixed(2));
  }, [amount, fromCurrency, toCurrency, currencies]);

  const handleSwap = () => {
    const prev = fromCurrency;
    setFromCurrency(toCurrency);
    setToCurrency(prev);
    if (convertedAmount) setAmount(convertedAmount);
  };

  const handleCopy = () => {
    if (!convertedAmount) return;
    navigator.clipboard.writeText(`${convertedAmount} ${toCurrency.code}`);
    setCopied(true);
    setTimeout(() => setCopied(false), 1500);
  };

  return (
    <div className="flex flex-col h-full max-w-md mx-auto w-full p-4 overflow-y-auto">
      <div className="flex items-center justify-between mb-4">
        <div>
          <h2 className="text-xl font-bold text-white">Currency Converter</h2>
          <p className="text-xs text-slate-400">{lastUpdated}</p>
        </div>
        <div className="flex items-center space-x-1.5">
          <button
            onClick={handleRefreshRates}
            disabled={isRefreshing}
            className="p-2 rounded-xl bg-slate-800 text-sky-400 hover:bg-slate-700 active:scale-95 transition-all"
            title="Refresh Live Exchange Rates"
          >
            <RefreshCw className={`w-4 h-4 ${isRefreshing ? 'animate-spin' : ''}`} />
          </button>
          <button
            onClick={handleSwap}
            className="p-2 rounded-xl bg-slate-800 text-sky-400 hover:bg-slate-700 active:scale-95 transition-all text-xs font-semibold flex items-center gap-1"
          >
            <ArrowLeftRight className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Exchange Rate Badge */}
      <div className="p-3 mb-4 rounded-xl bg-indigo-500/10 border border-indigo-500/20 text-indigo-300 text-xs flex items-center justify-between font-medium">
        <span>1 {fromCurrency.code} =</span>
        <span className="font-mono font-bold">
          {(toCurrency.rateAgainstUSD / fromCurrency.rateAgainstUSD).toFixed(4)} {toCurrency.code}
        </span>
      </div>

      {/* Conversion Cards */}
      <div className="space-y-3">
        {/* From Box */}
        <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-sm">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-medium text-slate-400 uppercase tracking-wider">You Pay</span>
            <select
              value={fromCurrency.code}
              onChange={(e) => {
                const c = currencies.find((item) => item.code === e.target.value);
                if (c) setFromCurrency(c);
              }}
              className="bg-slate-800 border border-slate-700 text-sky-400 text-sm rounded-lg px-2.5 py-1 outline-none font-bold"
            >
              {currencies.map((c) => (
                <option key={c.code} value={c.code}>
                  {c.code} - {c.name}
                </option>
              ))}
            </select>
          </div>
          <div className="flex items-center space-x-2">
            <span className="text-xl font-mono text-slate-400">{fromCurrency.symbol}</span>
            <input
              type="number"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              className="w-full bg-transparent text-white font-mono text-3xl font-bold outline-none"
              placeholder="0.00"
            />
          </div>
        </div>

        {/* To Box */}
        <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-sm">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-medium text-slate-400 uppercase tracking-wider">You Receive</span>
            <select
              value={toCurrency.code}
              onChange={(e) => {
                const c = currencies.find((item) => item.code === e.target.value);
                if (c) setToCurrency(c);
              }}
              className="bg-slate-800 border border-slate-700 text-sky-400 text-sm rounded-lg px-2.5 py-1 outline-none font-bold"
            >
              {currencies.map((c) => (
                <option key={c.code} value={c.code}>
                  {c.code} - {c.name}
                </option>
              ))}
            </select>
          </div>
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-2 truncate">
              <span className="text-xl font-mono text-slate-400">{toCurrency.symbol}</span>
              <span className="text-white font-mono text-3xl font-bold truncate">
                {convertedAmount || '0.00'}
              </span>
            </div>
            <button
              onClick={handleCopy}
              className="p-2 rounded-xl bg-slate-800 text-slate-300 hover:text-white hover:bg-slate-700 transition-all text-xs"
              title="Copy Converted Amount"
            >
              {copied ? <Check className="w-4 h-4 text-emerald-400" /> : <Copy className="w-4 h-4" />}
            </button>
          </div>
        </div>
      </div>

      {/* Quick Amount Pills */}
      <div className="flex items-center space-x-2 mt-3 overflow-x-auto pb-1 no-scrollbar">
        {['10', '50', '100', '500', '1000'].map((pill) => (
          <button
            key={pill}
            onClick={() => setAmount(pill)}
            className="px-3 py-1.5 rounded-lg bg-slate-800/90 text-slate-300 hover:bg-slate-700 hover:text-white text-xs font-mono font-medium transition-all"
          >
            {fromCurrency.symbol}{pill}
          </button>
        ))}
      </div>

      {/* Number Pad */}
      <div className="mt-4 grid grid-cols-3 gap-2">
        {['1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '0', 'C'].map((val) => (
          <button
            key={val}
            onClick={() => {
              if (val === 'C') {
                setAmount('');
              } else if (val === '.') {
                if (!amount.includes('.')) setAmount((prev) => prev + '.');
              } else {
                setAmount((prev) => (prev === '0' ? val : prev + val));
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
