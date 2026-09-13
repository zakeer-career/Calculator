package com.zakeercareer.calculator.data.currency

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CurrencyInfo(
    val code: String,
    val name: String,
    val flag: String
)

data class ExchangeRatesState(
    val base: String = "USD",
    val rates: Map<String, BigDecimal> = defaultRatesBigDecimal,
    val availableCurrencies: List<CurrencyInfo> = defaultCurrencies,
    val lastUpdated: String = "Offline Default Rates",
    val isRealtime: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

val defaultRatesBigDecimal: Map<String, BigDecimal> by lazy {
    defaultRates.mapValues { BigDecimal.valueOf(it.value) }
}

val defaultCurrencies = listOf(
    CurrencyInfo("USD", "US Dollar", "🇺🇸"),
    CurrencyInfo("EUR", "Euro", "🇪🇺"),
    CurrencyInfo("GBP", "British Pound", "🇬🇧"),
    CurrencyInfo("JPY", "Japanese Yen", "🇯🇵"),
    CurrencyInfo("CAD", "Canadian Dollar", "🇨🇦"),
    CurrencyInfo("AUD", "Australian Dollar", "🇦🇺"),
    CurrencyInfo("INR", "Indian Rupee", "🇮🇳"),
    CurrencyInfo("PKR", "Pakistani Rupee", "🇵🇰"),
    CurrencyInfo("BDT", "Bangladeshi Taka", "🇧🇩"),
    CurrencyInfo("CHF", "Swiss Franc", "🇨🇭"),
    CurrencyInfo("CNY", "Chinese Yuan", "🇨🇳"),
    CurrencyInfo("BRL", "Brazilian Real", "🇧🇷"),
    CurrencyInfo("ZAR", "South African Rand", "🇿🇦"),
    CurrencyInfo("MXN", "Mexican Peso", "🇲🇽"),
    CurrencyInfo("SGD", "Singapore Dollar", "🇸🇬"),
    CurrencyInfo("HKD", "Hong Kong Dollar", "🇭🇰"),
    CurrencyInfo("NZD", "New Zealand Dollar", "🇳🇿"),
    CurrencyInfo("KRW", "South Korean Won", "🇰🇷"),
    CurrencyInfo("SEK", "Swedish Krona", "🇸🇪"),
    CurrencyInfo("NOK", "Norwegian Krone", "🇳🇴"),
    CurrencyInfo("TRY", "Turkish Lira", "🇹🇷"),
    CurrencyInfo("AED", "UAE Dirham", "🇦🇪"),
    CurrencyInfo("SAR", "Saudi Riyal", "🇸🇦"),
    CurrencyInfo("QAR", "Qatari Riyal", "🇶🇦"),
    CurrencyInfo("KWD", "Kuwaiti Dinar", "🇰🇼"),
    CurrencyInfo("BHD", "Bahraini Dinar", "🇧🇭"),
    CurrencyInfo("OMR", "Omani Rial", "🇴🇲"),
    CurrencyInfo("THB", "Thai Baht", "🇹🇭"),
    CurrencyInfo("IDR", "Indonesian Rupiah", "🇮🇩"),
    CurrencyInfo("MYR", "Malaysian Ringgit", "🇲🇾"),
    CurrencyInfo("PHP", "Philippine Peso", "🇵🇭"),
    CurrencyInfo("EGP", "Egyptian Pound", "🇪🇬"),
    CurrencyInfo("PLN", "Polish Zloty", "🇵🇱"),
    CurrencyInfo("CZK", "Czech Koruna", "🇨🇿"),
    CurrencyInfo("HUF", "Hungarian Forint", "🇭🇺"),
    CurrencyInfo("ILS", "Israeli Shekel", "🇮🇱"),
    CurrencyInfo("DKK", "Danish Krone", "🇩🇰"),
    CurrencyInfo("VND", "Vietnamese Dong", "🇻🇳"),
    CurrencyInfo("LKR", "Sri Lankan Rupee", "🇱🇰"),
    CurrencyInfo("NPR", "Nepalese Rupee", "🇳🇵"),
    CurrencyInfo("NGN", "Nigerian Naira", "🇳🇬"),
    CurrencyInfo("KES", "Kenyan Shilling", "🇰🇪"),
    CurrencyInfo("DZD", "Algerian Dinar", "🇩🇿"),
    CurrencyInfo("MAD", "Moroccan Dirham", "🇲🇦"),
    CurrencyInfo("ARS", "Argentine Peso", "🇦🇷"),
    CurrencyInfo("CLP", "Chilean Peso", "🇨🇱"),
    CurrencyInfo("COP", "Colombian Peso", "🇨🇴"),
    CurrencyInfo("PEN", "Peruvian Sol", "🇵🇪"),
    CurrencyInfo("UAH", "Ukrainian Hryvnia", "🇺🇦"),
    CurrencyInfo("RON", "Romanian Leu", "🇷🇴"),
    CurrencyInfo("BGN", "Bulgarian Lev", "🇧🇬"),
    CurrencyInfo("TWD", "New Taiwan Dollar", "🇹🇼"),
    CurrencyInfo("MOP", "Macanese Pataca", "🇲🇴"),
    CurrencyInfo("GEL", "Georgian Lari", "🇬🇪"),
    CurrencyInfo("AMD", "Armenian Dram", "🇦🇲"),
    CurrencyInfo("IQD", "Iraqi Dinar", "🇮🇶"),
    CurrencyInfo("JOD", "Jordanian Dinar", "🇯🇴"),
    CurrencyInfo("LBP", "Lebanese Pound", "🇱🇧"),
    CurrencyInfo("MMK", "Myanmar Kyat", "🇲🇲"),
    CurrencyInfo("KHR", "Cambodian Riel", "🇰🇭"),
    CurrencyInfo("LAK", "Lao Kip", "🇱🇦"),
    CurrencyInfo("MNT", "Mongolian Tugrik", "🇲🇳"),
    CurrencyInfo("UZS", "Uzbekistan Som", "🇺🇿"),
    CurrencyInfo("KZT", "Kazakhstani Tenge", "🇰🇿"),
    CurrencyInfo("ETB", "Ethiopian Birr", "🇪🇹"),
    CurrencyInfo("TND", "Tunisian Dinar", "🇹🇳"),
    CurrencyInfo("GHS", "Ghanaian Cedi", "🇬🇭"),
    CurrencyInfo("UGX", "Ugandan Shilling", "🇺🇬"),
    CurrencyInfo("TZS", "Tanzanian Shilling", "🇹🇿"),
    CurrencyInfo("RWF", "Rwandan Franc", "🇷🇼"),
    CurrencyInfo("XAF", "Central African CFA Franc", "🇲🇱"),
    CurrencyInfo("XOF", "West African CFA Franc", "🇸🇳"),
    CurrencyInfo("UYU", "Uruguayan Peso", "🇺🇾"),
    CurrencyInfo("DOP", "Dominican Peso", "🇩🇴"),
    CurrencyInfo("CRC", "Costa Rican Colon", "🇨🇷"),
    CurrencyInfo("GTQ", "Guatemalan Quetzal", "🇬🇹"),
    CurrencyInfo("PAB", "Panamanian Balboa", "🇵🇦"),
    CurrencyInfo("BOB", "Bolivian Boliviano", "🇧🇴"),
    CurrencyInfo("PYG", "Paraguayan Guarani", "🇵🇾"),
    CurrencyInfo("ISK", "Icelandic Krona", "🇮🇸"),
    CurrencyInfo("RSD", "Serbian Dinar", "🇷🇸"),
    CurrencyInfo("BAM", "Bosnia Convertible Mark", "🇧🇦"),
    CurrencyInfo("FJD", "Fijian Dollar", "🇫🇯"),
    CurrencyInfo("PGK", "Papua New Guinean Kina", "🇵🇬")
)

val defaultRates = mapOf(
    "USD" to 1.0,
    "EUR" to 0.92,
    "GBP" to 0.78,
    "JPY" to 155.20,
    "CAD" to 1.36,
    "AUD" to 1.52,
    "INR" to 83.45,
    "PKR" to 278.50,
    "BDT" to 117.20,
    "CHF" to 0.90,
    "CNY" to 7.24,
    "BRL" to 5.25,
    "ZAR" to 18.50,
    "MXN" to 16.85,
    "SGD" to 1.35,
    "HKD" to 7.82,
    "NZD" to 1.65,
    "KRW" to 1370.0,
    "SEK" to 10.80,
    "NOK" to 10.95,
    "TRY" to 32.20,
    "AED" to 3.67,
    "SAR" to 3.75,
    "QAR" to 3.64,
    "KWD" to 0.31,
    "BHD" to 0.38,
    "OMR" to 0.38,
    "THB" to 36.80,
    "IDR" to 16200.0,
    "MYR" to 4.72,
    "PHP" to 57.80,
    "EGP" to 47.50,
    "PLN" to 4.02,
    "CZK" to 23.10,
    "HUF" to 360.50,
    "ILS" to 3.72,
    "DKK" to 6.88,
    "VND" to 25450.0,
    "LKR" to 302.50,
    "NPR" to 133.50,
    "NGN" to 1480.0,
    "KES" to 131.0,
    "DZD" to 134.5,
    "MAD" to 10.05,
    "ARS" to 890.0,
    "CLP" to 930.0,
    "COP" to 3880.0,
    "PEN" to 3.74,
    "UAH" to 40.2,
    "RON" to 4.58,
    "BGN" to 1.80,
    "TWD" to 32.4,
    "MOP" to 8.05,
    "GEL" to 2.80,
    "AMD" to 388.0,
    "IQD" to 1310.0,
    "JOD" to 0.709,
    "LBP" to 89500.0,
    "MMK" to 2100.0,
    "KHR" to 4080.0,
    "LAK" to 21800.0,
    "MNT" to 3450.0,
    "UZS" to 12650.0,
    "KZT" to 445.0,
    "ETB" to 57.5,
    "TND" to 3.12,
    "GHS" to 14.8,
    "UGX" to 3750.0,
    "TZS" to 2600.0,
    "RWF" to 1310.0,
    "XAF" to 605.0,
    "XOF" to 605.0,
    "UYU" to 38.8,
    "DOP" to 59.2,
    "CRC" to 525.0,
    "GTQ" to 7.78,
    "PAB" to 1.0,
    "BOB" to 6.91,
    "PYG" to 7520.0,
    "ISK" to 138.5,
    "RSD" to 108.0,
    "BAM" to 1.80,
    "FJD" to 2.26,
    "PGK" to 3.88
)

fun getFlagEmojiForCurrency(code: String): String {
    val uppercase = code.uppercase(Locale.US)
    val customFlags = mapOf(
        "EUR" to "🇪🇺", "USD" to "🇺🇸", "GBP" to "🇬🇧", "ANG" to "🇸🇽",
        "XCD" to "🇦🇮", "XOF" to "🇸🇳", "XAF" to "🇨🇲", "XPF" to "🇵🇫",
        "BTC" to "₿", "ETH" to "Ξ", "XAU" to "🥇", "XAG" to "🥈"
    )
    if (customFlags.containsKey(uppercase)) return customFlags[uppercase]!!
    if (uppercase.length >= 2) {
        val countryCode = uppercase.substring(0, 2)
        if (countryCode.all { it in 'A'..'Z' }) {
            val firstChar = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
            val secondChar = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
            return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
        }
    }
    return "🌐"
}

object CurrencyRepository {

    private const val PREFS_NAME = "currency_cache_prefs"
    private const val KEY_RATES_JSON = "cached_rates_json"
    private const val KEY_BASE = "cached_base"
    private const val KEY_TIMESTAMP = "cached_timestamp"

    fun saveCache(context: Context, state: ExchangeRatesState) {
        try {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val json = JSONObject()
            for ((code, rate) in state.rates) {
                json.put(code, rate.toPlainString())
            }
            prefs.edit()
                .putString(KEY_RATES_JSON, json.toString())
                .putString(KEY_BASE, state.base)
                .putString(KEY_TIMESTAMP, state.lastUpdated)
                .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadCache(context: Context): ExchangeRatesState? {
        return try {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val jsonStr = prefs.getString(KEY_RATES_JSON, null) ?: return null
            val base = prefs.getString(KEY_BASE, "USD") ?: "USD"
            val timestamp = prefs.getString(KEY_TIMESTAMP, "Persisted Offline Cache") ?: "Persisted Offline Cache"
            val json = JSONObject(jsonStr)

            val ratesMap = mutableMapOf<String, BigDecimal>()
            val dynamicCurrencies = defaultCurrencies.map {
                if (it.flag == "🌐") it.copy(flag = getFlagEmojiForCurrency(it.code)) else it
            }.toMutableList()
            val existingCodes = dynamicCurrencies.map { it.code }.toSet()

            for (key in json.keys()) {
                val rateStr = json.getString(key)
                val rate = BigDecimal(rateStr)
                ratesMap[key] = rate
                if (!existingCodes.contains(key)) {
                    dynamicCurrencies.add(CurrencyInfo(key, "$key Currency", getFlagEmojiForCurrency(key)))
                }
            }

            ExchangeRatesState(
                base = base,
                rates = ratesMap,
                availableCurrencies = dynamicCurrencies.sortedBy { it.code },
                lastUpdated = if (timestamp.startsWith("Cached")) timestamp else "Cached ($timestamp)",
                isRealtime = true,
                isLoading = false,
                error = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchRealtimeRates(context: Context? = null): ExchangeRatesState = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL("https://open.er-api.com/v6/latest/USD")
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 6000
            connection.readTimeout = 6000

            if (connection.responseCode == 200) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(responseText)
                val ratesObj = json.getJSONObject("rates")

                val ratesMap = mutableMapOf<String, BigDecimal>()
                val dynamicCurrencies = defaultCurrencies.map {
                    if (it.flag == "🌐") it.copy(flag = getFlagEmojiForCurrency(it.code)) else it
                }.toMutableList()
                val existingCodes = dynamicCurrencies.map { it.code }.toSet()

                for (key in ratesObj.keys()) {
                    val rate = BigDecimal(ratesObj.optString(key, ratesObj.getDouble(key).toString()))
                    ratesMap[key] = rate
                    if (!existingCodes.contains(key)) {
                        dynamicCurrencies.add(CurrencyInfo(key, "$key Currency", getFlagEmojiForCurrency(key)))
                    }
                }

                val df = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                val timeStr = df.format(Date())

                val state = ExchangeRatesState(
                    base = "USD",
                    rates = ratesMap,
                    availableCurrencies = dynamicCurrencies.sortedBy { it.code },
                    lastUpdated = "Live ($timeStr)",
                    isRealtime = true,
                    isLoading = false,
                    error = null
                )

                if (context != null) {
                    saveCache(context, state)
                }

                state
            } else {
                val cached = context?.let { loadCache(it) }
                if (cached != null) {
                    cached.copy(error = "HTTP ${connection.responseCode}. Using persisted cache.")
                } else {
                    ExchangeRatesState(
                        base = "USD",
                        rates = defaultRatesBigDecimal,
                        availableCurrencies = defaultCurrencies,
                        lastUpdated = "Offline (Default)",
                        isRealtime = false,
                        isLoading = false,
                        error = "HTTP ${connection.responseCode}. Using fallback rates."
                    )
                }
            }
        } catch (e: Exception) {
            val cached = context?.let { loadCache(it) }
            if (cached != null) {
                cached.copy(error = "Network offline. Using persisted cache (${cached.lastUpdated}).")
            } else {
                ExchangeRatesState(
                    base = "USD",
                    rates = defaultRatesBigDecimal,
                    availableCurrencies = defaultCurrencies,
                    lastUpdated = "Offline Fallback",
                    isRealtime = false,
                    isLoading = false,
                    error = "Network offline. Using fallback exchange rates."
                )
            }
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Enforce BigDecimal arithmetic for precision financial & currency conversions,
     * avoiding IEEE 754 floating-point artifacts.
     */
    fun convertCurrencyBigDecimal(
        amount: BigDecimal,
        fromCode: String,
        toCode: String,
        rates: Map<String, BigDecimal>,
        scale: Int = 4
    ): BigDecimal {
        if (amount.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO.setScale(scale, RoundingMode.HALF_UP)
        val fromRate = rates[fromCode] ?: defaultRatesBigDecimal[fromCode]
            ?: throw IllegalArgumentException("Exchange rate unavailable for currency '$fromCode'")
        val toRate = rates[toCode] ?: defaultRatesBigDecimal[toCode]
            ?: throw IllegalArgumentException("Exchange rate unavailable for currency '$toCode'")

        // Amount in USD = Amount / fromRate
        val inUsd = amount.divide(fromRate, MathContext.DECIMAL128)
        val converted = inUsd.multiply(toRate)
        return converted.setScale(scale, RoundingMode.HALF_UP)
    }

    @JvmName("convertCurrencyBigDecimalWithDoubleRates")
    fun convertCurrencyBigDecimal(
        amount: BigDecimal,
        fromCode: String,
        toCode: String,
        rates: Map<String, Double>,
        scale: Int = 4
    ): BigDecimal {
        val bigDecimalRates = rates.mapValues { BigDecimal.valueOf(it.value) }
        return convertCurrencyBigDecimal(amount, fromCode, toCode, bigDecimalRates, scale)
    }

    fun convertCurrency(
        amount: Double,
        fromCode: String,
        toCode: String,
        rates: Map<String, BigDecimal>
    ): Double {
        val amountBigDecimal = BigDecimal.valueOf(amount)
        return convertCurrencyBigDecimal(amountBigDecimal, fromCode, toCode, rates, scale = 6).toDouble()
    }

    @JvmName("convertCurrencyWithDoubleRates")
    fun convertCurrency(
        amount: Double,
        fromCode: String,
        toCode: String,
        rates: Map<String, Double>
    ): Double {
        val bigDecimalRates = rates.mapValues { BigDecimal.valueOf(it.value) }
        return convertCurrency(amount, fromCode, toCode, bigDecimalRates)
    }

    fun convertCurrencyOrNull(
        amount: Double,
        fromCode: String,
        toCode: String,
        rates: Map<String, BigDecimal>
    ): Double? {
        return try {
            convertCurrency(amount, fromCode, toCode, rates)
        } catch (e: Exception) {
            null
        }
    }

    @JvmName("convertCurrencyOrNullWithDoubleRates")
    fun convertCurrencyOrNull(
        amount: Double,
        fromCode: String,
        toCode: String,
        rates: Map<String, Double>
    ): Double? {
        return try {
            convertCurrency(amount, fromCode, toCode, rates)
        } catch (e: Exception) {
            null
        }
    }
}
