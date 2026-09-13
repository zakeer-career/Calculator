import { UnitCategory, UnitItem } from '../types';

export const UNIT_CATEGORIES: UnitCategory[] = [
  'Length',
  'Mass',
  'Temperature',
  'Area',
  'Volume',
  'Speed',
  'Time',
  'Pressure',
  'Energy',
  'Data Storage',
];

export const UNIT_DATA: Record<UnitCategory, UnitItem[]> = {
  Length: [
    { name: 'Meter', symbol: 'm', factorToBase: 1.0 },
    { name: 'Kilometer', symbol: 'km', factorToBase: 1000.0 },
    { name: 'Centimeter', symbol: 'cm', factorToBase: 0.01 },
    { name: 'Millimeter', symbol: 'mm', factorToBase: 0.001 },
    { name: 'Mile', symbol: 'mi', factorToBase: 1609.344 },
    { name: 'Yard', symbol: 'yd', factorToBase: 0.9144 },
    { name: 'Foot', symbol: 'ft', factorToBase: 0.3048 },
    { name: 'Inch', symbol: 'in', factorToBase: 0.0254 },
  ],
  Mass: [
    { name: 'Kilogram', symbol: 'kg', factorToBase: 1.0 },
    { name: 'Gram', symbol: 'g', factorToBase: 0.001 },
    { name: 'Milligram', symbol: 'mg', factorToBase: 0.000001 },
    { name: 'Pound', symbol: 'lb', factorToBase: 0.45359237 },
    { name: 'Ounce', symbol: 'oz', factorToBase: 0.028349523125 },
    { name: 'Metric Ton', symbol: 't', factorToBase: 1000.0 },
  ],
  Temperature: [
    { name: 'Celsius', symbol: '°C', factorToBase: 1.0 },
    { name: 'Fahrenheit', symbol: '°F', factorToBase: 1.0 },
    { name: 'Kelvin', symbol: 'K', factorToBase: 1.0 },
  ],
  Area: [
    { name: 'Square Meter', symbol: 'm²', factorToBase: 1.0 },
    { name: 'Square Kilometer', symbol: 'km²', factorToBase: 1000000.0 },
    { name: 'Square Foot', symbol: 'ft²', factorToBase: 0.09290304 },
    { name: 'Acre', symbol: 'ac', factorToBase: 4046.8564224 },
    { name: 'Hectare', symbol: 'ha', factorToBase: 10000.0 },
  ],
  Volume: [
    { name: 'Liter', symbol: 'L', factorToBase: 1.0 },
    { name: 'Milliliter', symbol: 'mL', factorToBase: 0.001 },
    { name: 'Cubic Meter', symbol: 'm³', factorToBase: 1000.0 },
    { name: 'US Gallon', symbol: 'gal', factorToBase: 3.785411784 },
    { name: 'US Cup', symbol: 'cup', factorToBase: 0.2365882365 },
    { name: 'Fluid Ounce', symbol: 'fl oz', factorToBase: 0.0295735295625 },
  ],
  Speed: [
    { name: 'Meter / second', symbol: 'm/s', factorToBase: 1.0 },
    { name: 'Kilometer / hour', symbol: 'km/h', factorToBase: 0.277777778 },
    { name: 'Miles / hour', symbol: 'mph', factorToBase: 0.44704 },
    { name: 'Knot', symbol: 'kt', factorToBase: 0.514444 },
  ],
  Time: [
    { name: 'Second', symbol: 's', factorToBase: 1.0 },
    { name: 'Minute', symbol: 'min', factorToBase: 60.0 },
    { name: 'Hour', symbol: 'h', factorToBase: 3600.0 },
    { name: 'Day', symbol: 'd', factorToBase: 86400.0 },
    { name: 'Week', symbol: 'wk', factorToBase: 604800.0 },
  ],
  Pressure: [
    { name: 'Pascal', symbol: 'Pa', factorToBase: 1.0 },
    { name: 'Bar', symbol: 'bar', factorToBase: 100000.0 },
    { name: 'Atmosphere', symbol: 'atm', factorToBase: 101325.0 },
    { name: 'PSI', symbol: 'psi', factorToBase: 6894.757293168 },
  ],
  Energy: [
    { name: 'Joule', symbol: 'J', factorToBase: 1.0 },
    { name: 'Kilojoule', symbol: 'kJ', factorToBase: 1000.0 },
    { name: 'Kilocalorie', symbol: 'kcal', factorToBase: 4184.0 },
    { name: 'Watt-hour', symbol: 'Wh', factorToBase: 3600.0 },
    { name: 'Kilowatt-hour', symbol: 'kWh', factorToBase: 3600000.0 },
  ],
  'Data Storage': [
    { name: 'Byte', symbol: 'B', factorToBase: 1.0 },
    { name: 'Kilobyte', symbol: 'KB', factorToBase: 1024.0 },
    { name: 'Megabyte', symbol: 'MB', factorToBase: 1048576.0 },
    { name: 'Gigabyte', symbol: 'GB', factorToBase: 1073741824.0 },
    { name: 'Terabyte', symbol: 'TB', factorToBase: 1099511627776.0 },
  ],
};

export function convertUnits(
  category: UnitCategory,
  fromUnit: UnitItem,
  toUnit: UnitItem,
  value: number
): number {
  if (isNaN(value)) return 0;
  if (fromUnit.name === toUnit.name) return value;

  if (category === 'Temperature') {
    // Convert from -> Celsius
    let celsius = value;
    if (fromUnit.name === 'Fahrenheit') {
      celsius = ((value - 32) * 5) / 9;
    } else if (fromUnit.name === 'Kelvin') {
      celsius = value - 273.15;
    }

    // Convert Celsius -> to
    if (toUnit.name === 'Celsius') return celsius;
    if (toUnit.name === 'Fahrenheit') return (celsius * 9) / 5 + 32;
    if (toUnit.name === 'Kelvin') return celsius + 273.15;
    return celsius;
  }

  // Base unit conversion: value * fromFactor / toFactor
  const baseValue = value * fromUnit.factorToBase;
  return baseValue / toUnit.factorToBase;
}
