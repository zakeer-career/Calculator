package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.currency.CurrencyRepository
import com.example.data.currency.ExchangeRatesState
import com.example.data.currency.defaultRates
import com.example.data.db.AppDatabase
import com.example.data.db.CalculationEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException
import java.math.BigDecimal

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var context: Context

    @Before
    fun createDb() {
        context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun `read string from context`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("Calculator", appName)
    }

    @Test
    fun `test targeted Room updates and scoped search`() = runBlocking {
        val dao = db.calculationDao()

        val id1 = dao.insert(
            CalculationEntity(
                category = "CALCULATOR",
                expression = "10 + 20",
                result = "30"
            )
        )
        val id2 = dao.insert(
            CalculationEntity(
                category = "CURRENCY",
                expression = "100 USD -> EUR",
                result = "92.00 EUR"
            )
        )

        // Targeted favorite update
        dao.updateFavorite(id1, true)
        val favs = dao.getFavorites().first()
        assertEquals(1, favs.size)
        assertEquals(id1, favs[0].id)
        assertTrue(favs[0].isFavorite)

        // Targeted note update
        dao.updateNote(id1, "important calculation")
        val updatedEntry = dao.getAllHistory().first().first { it.id == id1 }
        assertEquals("important calculation", updatedEntry.note)

        // Scoped category search
        val searchResult = dao.searchHistoryByCategory("USD", "CURRENCY").first()
        assertEquals(1, searchResult.size)
        assertEquals(id2, searchResult[0].id)

        // Scoped favorites search
        val favSearch = dao.searchFavorites("10").first()
        assertEquals(1, favSearch.size)
        assertEquals(id1, favSearch[0].id)

        // Targeted trash status update
        dao.setTrashStatus(id1, true)
        val nonTrashList = dao.getAllHistory().first()
        assertEquals(1, nonTrashList.size)
        assertEquals(id2, nonTrashList[0].id)

        val trashList = dao.getTrashHistory().first()
        assertEquals(1, trashList.size)
        assertEquals(id1, trashList[0].id)

        // Restore from trash
        dao.setTrashStatus(id1, false)
        assertEquals(2, dao.getAllHistory().first().size)
    }

    @Test
    fun `test scoped history deletion`() = runBlocking {
        val dao = db.calculationDao()

        dao.insert(CalculationEntity(category = "CALCULATOR", expression = "2 * 5", result = "10"))
        dao.insert(CalculationEntity(category = "CALCULATOR", expression = "2 * 10", result = "20"))
        dao.insert(CalculationEntity(category = "UNIT", expression = "2 m -> cm", result = "200 cm"))

        // Scoped clear by search and category
        dao.clearBySearchAndCategory("10", "CALCULATOR")
        val remaining = dao.getAllHistory().first()
        assertEquals(2, remaining.size)
        assertFalse(remaining.any { it.expression == "2 * 10" })
    }

    @Test
    fun `test persistent currency cache loading and saving`() {
        val testRates = mapOf(
            "USD" to BigDecimal("1.0"),
            "EUR" to BigDecimal("0.95")
        )
        val state = ExchangeRatesState(
            rates = testRates,
            lastUpdated = "TestDate"
        )
        CurrencyRepository.saveCache(context, state)

        val loaded = CurrencyRepository.loadCache(context)
        assertNotNull(loaded)
        assertEquals("TestDate", loaded?.lastUpdated)
        assertEquals(BigDecimal("0.95"), loaded?.rates?.get("EUR"))
    }
}
