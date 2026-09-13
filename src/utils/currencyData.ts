import { CurrencyRate } from '../types';

export const DEFAULT_CURRENCIES: CurrencyRate[] = [
  { code: 'USD', name: 'US Dollar', symbol: '$', rateAgainstUSD: 1.0 },
  { code: 'EUR', name: 'Euro', symbol: '€', rateAgainstUSD: 0.92 },
  { code: 'GBP', name: 'British Pound', symbol: '£', rateAgainstUSD: 0.79 },
  { code: 'JPY', name: 'Japanese Yen', symbol: '¥', rateAgainstUSD: 154.2 },
  { code: 'CAD', name: 'Canadian Dollar', symbol: 'CA$', rateAgainstUSD: 1.36 },
  { code: 'AUD', name: 'Australian Dollar', symbol: 'AU$', rateAgainstUSD: 1.52 },
  { code: 'CHF', name: 'Swiss Franc', symbol: 'CHF', rateAgainstUSD: 0.89 },
  { code: 'CNY', name: 'Chinese Yuan', symbol: '¥', rateAgainstUSD: 7.24 },
  { code: 'INR', name: 'Indian Rupee', symbol: '₹', rateAgainstUSD: 83.5 },
  { code: 'PKR', name: 'Pakistani Rupee', symbol: '₨', rateAgainstUSD: 278.4 },
  { code: 'AED', name: 'UAE Dirham', symbol: 'AED', rateAgainstUSD: 3.67 },
  { code: 'SAR', name: 'Saudi Riyal', symbol: 'SAR', rateAgainstUSD: 3.75 },
  { code: 'SGD', name: 'Singapore Dollar', symbol: 'S$', rateAgainstUSD: 1.35 },
  { code: 'BRL', name: 'Brazilian Real', symbol: 'R$', rateAgainstUSD: 5.45 },
  { code: 'KRW', name: 'South Korean Won', symbol: '₩', rateAgainstUSD: 1375.0 },
];

export async function fetchLiveRates(): Promise<Record<string, number> | null> {
  try {
    const res = await fetch('https://open.er-api.com/v6/latest/USD');
    if (!res.ok) return null;
    const data = await res.json();
    return data.rates || null;
  } catch {
    return null;
  }
}
