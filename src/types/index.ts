export type CalculatorTab = 'standard' | 'scientific' | 'converter' | 'currency' | 'matrix' | 'history' | 'settings';

export interface HistoryItem {
  id: string;
  expression: string;
  result: string;
  timestamp: number;
}

export type UnitCategory = 
  | 'Length' 
  | 'Mass' 
  | 'Temperature' 
  | 'Area' 
  | 'Volume' 
  | 'Speed' 
  | 'Time' 
  | 'Pressure' 
  | 'Energy' 
  | 'Data Storage';

export interface UnitItem {
  name: string;
  symbol: string;
  factorToBase: number;
}

export interface CurrencyRate {
  code: string;
  name: string;
  symbol: string;
  rateAgainstUSD: number;
}

export type MatrixDimensions = '2x2' | '3x3';
export type MatrixOperation = 'add' | 'subtract' | 'multiply' | 'detA' | 'detB' | 'transposeA' | 'transposeB' | 'inverseA' | 'inverseB';

export type AppThemePreset = 'liquid_glass' | 'material_you' | 'amoled' | 'nord' | 'cyberpunk' | 'emerald';
export type NumberFormatStyle = 'STANDARD' | 'EUROPEAN' | 'INDIAN';
