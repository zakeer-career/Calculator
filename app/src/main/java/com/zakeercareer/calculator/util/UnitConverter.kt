package com.zakeercareer.calculator.util

enum class UnitCategory(val displayName: String) {
    LENGTH("Length"),
    MASS("Mass"),
    TEMPERATURE("Temperature"),
    AREA("Area"),
    VOLUME("Volume"),
    SPEED("Speed"),
    TIME("Time"),
    PRESSURE("Pressure"),
    ENERGY("Energy"),
    DATA("Data Storage")
}

data class UnitItem(val name: String, val symbol: String, val factorToBase: Double)

object UnitConverter {

    fun getUnits(category: UnitCategory): List<UnitItem> {
        return when (category) {
            UnitCategory.LENGTH -> listOf(
                UnitItem("Meter", "m", 1.0),
                UnitItem("Kilometer", "km", 1000.0),
                UnitItem("Centimeter", "cm", 0.01),
                UnitItem("Millimeter", "mm", 0.001),
                UnitItem("Mile", "mi", 1609.344),
                UnitItem("Yard", "yd", 0.9144),
                UnitItem("Foot", "ft", 0.3048),
                UnitItem("Inch", "in", 0.0254)
            )
            UnitCategory.MASS -> listOf(
                UnitItem("Kilogram", "kg", 1.0),
                UnitItem("Gram", "g", 0.001),
                UnitItem("Milligram", "mg", 0.000001),
                UnitItem("Pound", "lb", 0.45359237),
                UnitItem("Ounce", "oz", 0.028349523125),
                UnitItem("Metric Ton", "t", 1000.0)
            )
            UnitCategory.TEMPERATURE -> listOf(
                UnitItem("Celsius", "°C", 1.0),
                UnitItem("Fahrenheit", "°F", 1.0),
                UnitItem("Kelvin", "K", 1.0)
            )
            UnitCategory.AREA -> listOf(
                UnitItem("Square Meter", "m²", 1.0),
                UnitItem("Square Kilometer", "km²", 1000000.0),
                UnitItem("Square Foot", "ft²", 0.09290304),
                UnitItem("Acre", "ac", 4046.8564224),
                UnitItem("Hectare", "ha", 10000.0)
            )
            UnitCategory.VOLUME -> listOf(
                UnitItem("Liter", "L", 1.0),
                UnitItem("Milliliter", "mL", 0.001),
                UnitItem("Cubic Meter", "m³", 1000.0),
                UnitItem("US Gallon", "gal", 3.785411784),
                UnitItem("US Cup", "cup", 0.2365882365),
                UnitItem("Fluid Ounce", "fl oz", 0.0295735295625)
            )
            UnitCategory.SPEED -> listOf(
                UnitItem("Meter / second", "m/s", 1.0),
                UnitItem("Kilometer / hour", "km/h", 1.0 / 3.6),
                UnitItem("Miles / hour", "mph", 0.44704),
                UnitItem("Knot", "kt", 1852.0 / 3600.0)
            )
            UnitCategory.TIME -> listOf(
                UnitItem("Second", "s", 1.0),
                UnitItem("Minute", "min", 60.0),
                UnitItem("Hour", "h", 3600.0),
                UnitItem("Day", "d", 86400.0),
                UnitItem("Week", "wk", 604800.0)
            )
            UnitCategory.PRESSURE -> listOf(
                UnitItem("Pascal", "Pa", 1.0),
                UnitItem("Bar", "bar", 100000.0),
                UnitItem("Atmosphere", "atm", 101325.0),
                UnitItem("PSI", "psi", 6894.757293168)
            )
            UnitCategory.ENERGY -> listOf(
                UnitItem("Joule", "J", 1.0),
                UnitItem("Kilojoule", "kJ", 1000.0),
                UnitItem("Calorie", "cal", 4.184),
                UnitItem("Kilocalorie", "kcal", 4184.0),
                UnitItem("Watt-hour", "Wh", 3600.0)
            )
            UnitCategory.DATA -> listOf(
                UnitItem("Byte", "B", 1.0),
                UnitItem("Kilobyte", "KB", 1024.0),
                UnitItem("Megabyte", "MB", 1048576.0),
                UnitItem("Gigabyte", "GB", 1073741824.0),
                UnitItem("Terabyte", "TB", 1099511627776.0)
            )
        }
    }

    fun convert(
        value: Double,
        category: UnitCategory,
        fromUnit: UnitItem,
        toUnit: UnitItem
    ): Double {
        if (category == UnitCategory.TEMPERATURE) {
            return convertTemperature(value, fromUnit.symbol, toUnit.symbol)
        }
        val baseValue = value * fromUnit.factorToBase
        return baseValue / toUnit.factorToBase
    }

    private fun convertTemperature(value: Double, fromSymbol: String, toSymbol: String): Double {
        if (fromSymbol == toSymbol) return value
        val celsius = when (fromSymbol) {
            "°C" -> value
            "°F" -> (value - 32.0) * 5.0 / 9.0
            "K" -> value - 273.15
            else -> value
        }
        return when (toSymbol) {
            "°C" -> celsius
            "°F" -> (celsius * 9.0 / 5.0) + 32.0
            "K" -> celsius + 273.15
            else -> celsius
        }
    }
}
